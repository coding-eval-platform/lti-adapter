package ar.edu.itba.cep.lti_service.domain.managers;

import ar.edu.itba.cep.lti.*;
import ar.edu.itba.cep.lti_service.domain.helpers.*;
import ar.edu.itba.cep.lti_service.domain.helpers.LtiDeepLinkingResponseHelper.LtiResourceLink;
import ar.edu.itba.cep.lti_service.domain.helpers.LtiDeepLinkingResponseHelper.LtiResourceLink.LineItem;
import ar.edu.itba.cep.lti_service.external_cep_services.evaluations_service.EvaluationsService;
import ar.edu.itba.cep.lti_service.external_cep_services.evaluations_service.Exam;
import ar.edu.itba.cep.lti_service.external_cep_services.tokens_service.TokensService;
import ar.edu.itba.cep.lti_service.external_lti_web_services.LtiAssignmentAndGradeServicesClient;
import ar.edu.itba.cep.lti_service.models.ExamTaking;
import ar.edu.itba.cep.lti_service.models.ToolDeployment;
import ar.edu.itba.cep.lti_service.repositories.ExamTakingRepository;
import ar.edu.itba.cep.lti_service.repositories.ToolDeploymentRepository;
import ar.edu.itba.cep.lti_service.services.LtiBadRequestException;
import ar.edu.itba.cep.lti_service.services.LtiService;
import ar.edu.itba.cep.roles.Role;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static ar.edu.itba.cep.lti_service.domain.helpers.LtiDeepLinkingRequestHelper.DeepLinkingContentType.LTI_RESOURCE_LINK;

/**
 * Manager in charge of providing services that allows LTI 1.3 interaction.
 */
@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class LtiAppManager implements LtiService {

    private static final String EXAM_ID_CUSTOM = "exam-id";
    private static final String RETURN_URL_LAUNCH_PRESENTATION = "return_url";
    private static final String RESOURCE_LINK_REQUEST = "LtiResourceLinkRequest";


    private final ToolDeploymentRepository toolDeploymentRepository;
    private final ExamTakingRepository examTakingRepository;

    private final LtiStateHelper ltiStateHelper;
    private final LtiMessageDeserializer ltiMessageDeserializer;
    private final LtiMessageValidator ltiMessageValidator;
    private final LtiDeepLinkingRequestHelper ltiDeepLinkingRequestHelper;
    private final ExamSelectionStateHelper examSelectionStateHelper;
    private final LtiMessageSerializer ltiMessageSerializer;
    private final LtiDeepLinkingResponseHelper ltiDeepLinkingResponseHelper;

    private final LtiAssignmentAndGradeServicesHelper ltiAssignmentAndGradeServicesHelper;

    private final EvaluationsService evaluationsService;
    private final TokensService tokensService;

    private final LtiAssignmentAndGradeServicesClient ltiAssignmentAndGradeServicesClient;


    @Override
    public AuthenticationRequest loginInitiation(final LoginInitiationRequest loginInitiationRequest) {
        final var issuer = loginInitiationRequest.getIssuer(); // This is never null

        // Note that, in both cases
        // (the deployment id is not present, or the deployment id and the client id are not present),
        // more than one ToolDeployment might exist. In those cases, the first to be retrieved is used.
        final ToolDeployment toolDeployment = Optional.ofNullable(loginInitiationRequest.getClientId())
                .flatMap(clientId -> Optional.ofNullable(loginInitiationRequest.getDeploymentId())
                        .map(deploymentId -> toolDeploymentRepository.find(deploymentId, clientId, issuer))
                        .orElseGet(() -> toolDeploymentRepository.find(clientId, issuer).stream().findFirst())
                )
                .or(() -> toolDeploymentRepository.find(issuer).stream().findFirst())
                .orElseThrow(NoSuchEntityException::new);

        // We must generate a nonce
        final String nonce = UUID.randomUUID().toString();
        // We must set the ToolDeployment identifier as state
        final var stateData = LtiStateHelper.StateData.create(toolDeployment.getId(), nonce);
        final var state = ltiStateHelper.encode(stateData);

        // Build the authentication request.
        return new AuthenticationRequest(
                toolDeployment.getOidcAuthenticationEndpoint(),
                toolDeployment.getClientId(),
                loginInitiationRequest.getLoginHint(),
                loginInitiationRequest.getTargetLinkUri(),
                nonce,
                loginInitiationRequest.getLtiMessageHint(),
                state
        );
    }


    @Override
    public ExamSelectionResponse examSelection(final AuthenticationResponse authenticationResponse) {
        return this.handleAuthenticationResponse(authenticationResponse, this::examSelection);
    }


    @Override
    public ExamSelectedResponse examSelected(final ExamSelectedRequest examSelectedRequest) {
        return evaluationsService.getExamById(examSelectedRequest.getExamId())
                .map(exam -> existingExam(exam, examSelectedRequest))
                .orElseGet(NonExistingExamSelectedResponse::getInstance)
                ;
    }

    @Override
    @Transactional
    public ExamTakingResponse takeExam(final AuthenticationResponse authenticationResponse) {
        return this.handleAuthenticationResponse(authenticationResponse, this::takeExam);
    }

    @Override
    @Transactional
    public void scoreExam(final ExamScoringRequest request) {
        examTakingRepository.get(request.getExamId(), request.getSubject()).ifPresentOrElse(
                examTaking -> ltiAssignmentAndGradeServicesClient.publishScore(examTaking, request.getScore()),
                () -> {
                    throw new IllegalStateException("No Exam Taking with the given arguments");
                }
        );
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * Handles the given {@code response}, continuing the flow with the given {@code andThen} {@link MessageHandler}.
     *
     * @param response The {@link AuthenticationResponse} to be handled.
     * @param andThen  The {@link MessageHandler} used to perform the actual handling operation.
     * @param <C>      The concrete type of object to be returned
     * @return The object resulting of the handling.
     */
    private <C> C handleAuthenticationResponse(final AuthenticationResponse response, final MessageHandler<C> andThen) {
        final var stateData = ltiStateHelper.decode(response.getState());
        final var toolDeployment = toolDeploymentRepository
                .findById(stateData.getToolDeploymentId())
                .orElseThrow(IllegalStateException::new);
        final var ltiMessage = ltiMessageDeserializer.deserialize(response.getIdToken(), toolDeployment);
        ltiMessageValidator.validateLtiMessage(toolDeployment, stateData.getNonce(), ltiMessage);
        return andThen.handle(toolDeployment, ltiMessage);
    }


    // ================== Message handlers ===================

    /**
     * Performs the exam creation initiation step of the Deep Linking flow.
     *
     * @param toolDeployment The {@link ToolDeployment} representing the integration exchanging the message.
     * @param ltiMessage     A {@link Map} representing the LTI message.
     * @return The {@link ExamSelectionResponse} resulting of the given {@code ltiMessage}.
     */
    private ExamSelectionResponse examSelection(final ToolDeployment toolDeployment, final Map<String, Object> ltiMessage) {

        // First get the deep linking settings and the assignment and grades services capabilities,
        // and validate that exams can be created.
        final var settings = ltiDeepLinkingRequestHelper.extractDeepLinkingSettings(ltiMessage);
        final var capabilities = ltiAssignmentAndGradeServicesHelper.extractCapabilities(ltiMessage);
        validateExamCanBeSelected(settings, capabilities);


        // Then create the state that needs to be sent to the UA.
        final var examSelectionStateData = ExamSelectionStateHelper.ExamSelectionStateData.create(
                settings.getReturnUrl(),
                settings.getData(),
                toolDeployment.getId(),
                UUID.randomUUID().toString()
        );
        final var state = examSelectionStateHelper.encode(examSelectionStateData);

        return new ExamSelectionResponse(state);
    }

    /**
     * Handles the given {@code request} in case the id of the exam it carries exists.
     *
     * @param exam    The {@link Exam}.
     * @param request The {@link ExamSelectedRequest} with needed data to handle this case.
     * @return An {@link ExistingExamSelectedResponse} if everything is OK, or a {@link NotUpcomingExamSelectedResponse}
     * if the {@link Exam}'s state is not {@link Exam.State#UPCOMING}.
     */
    private ExamSelectedResponse existingExam(final Exam exam, final ExamSelectedRequest request) {
        if (exam.getState() != Exam.State.UPCOMING) {
            return NotUpcomingExamSelectedResponse.getInstance();
        }
        final var state = examSelectionStateHelper.decode(request.getState());
        final var toolDeployment = toolDeploymentRepository
                .findById(state.getToolDeploymentId())
                .orElseThrow(IllegalStateException::new);
        final var ltiResourceLinkBuilder = adaptExamToLti(exam).url(request.getUrl());
        Optional.ofNullable(request.getIcon()).map(LtiAppManager::mapImage).ifPresent(ltiResourceLinkBuilder::icon);
        Optional.ofNullable(request.getThumbnail()).map(LtiAppManager::mapImage).ifPresent(ltiResourceLinkBuilder::thumbnail);


        final var ltiMessage = ltiDeepLinkingResponseHelper.buildMessage(
                toolDeployment,
                state.getData(),
                List.of(ltiResourceLinkBuilder.build())
        );

        return new ExistingExamSelectedResponse(
                state.getReturnUrl(),
                ltiMessageSerializer.serialize(ltiMessage, toolDeployment),
                examExamToExamData(exam)
        );
    }

    private ExamTakingResponse takeExam(final ToolDeployment toolDeployment, final Map<String, Object> ltiMessage) {
        // First check if the message is an LtiResourceLinkRequest
        if (!RESOURCE_LINK_REQUEST.equals(ltiMessage.get(LtiConstants.LtiClaims.MESSAGE_TYPE))) {
            throw new LtiBadRequestException("Message type should be an LtiResourceLinkRequest");
        }

        // If yes, extract capabilities in order to check if the score can be pushed into Campus
        final var capabilities = ltiAssignmentAndGradeServicesHelper.extractCapabilities(ltiMessage);
        validateScoreScope(capabilities);

        // If control reached here, then the message contains the score scope, which indicates that the score
        // can be pushed into the LMS.

        // We proceed to process the message...
        // First extract stuff to be stored for later retrieval for when the score publishing process is executed.
        final var examId = getCustomProperties(ltiMessage)
                .flatMap(m -> Optional.ofNullable(m.get(EXAM_ID_CUSTOM)))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(str -> {
                    try {
                        return Long.valueOf(str);
                    } catch (final NumberFormatException e) {
                        return null;
                    }
                })
                .orElseThrow(() -> new RuntimeException("Missing exam id"));
        final var exam = evaluationsService.getExamById(examId)
                .orElseThrow(() -> new IllegalStateException("Could not retrieve the exam. Maybe was deleted?"));


        final var userId = Optional.ofNullable(ltiMessage.get(LtiConstants.LtiClaims.SUBJECT))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElseThrow(() -> new LtiBadRequestException("Missing user id"));
        final var lineItemUrl = capabilities.getLineItem();
        if (lineItemUrl == null) {
            throw new LtiBadRequestException("Missing lineitem capability");
        }
        if (!examTakingRepository.exists(examId, userId)) {
            final var examTaking = ExamTaking.withoutId(examId, userId, lineItemUrl, exam.getMaxScore(), toolDeployment);
            examTakingRepository.save(examTaking);
        }

        // Then get other needed stuff for the response.
        final var tokenData = tokensService.tokenFor(userId, Set.of(Role.STUDENT))
                .orElseThrow(() -> new IllegalStateException("Could not get token"));
        final var returnUrl = launchPresentation(ltiMessage)
                .flatMap(m -> Optional.ofNullable(m.get(RETURN_URL_LAUNCH_PRESENTATION)))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .orElse(null);

        // Finally, build the response
        return new ExamTakingResponse(
                examId,
                tokenData.getId(),
                tokenData.getAccessToken(),
                tokenData.getRefreshToken(),
                returnUrl
        );
    }

    // ================== Other helpers ===================

    /**
     * Validates that the tool can link an exam in the LMS.
     *
     * @param settings     The {@link LtiDeepLinkingRequestHelper.DeepLinkingSettings} to be analyzed.
     * @param capabilities The {@link LtiAssignmentAndGradeServicesHelper.AssignmentAndGradeServicesCapabilities}
     *                     to be analyzed.
     * @throws RuntimeException If an exam cannot be created.
     */
    private static void validateExamCanBeSelected(
            final LtiDeepLinkingRequestHelper.DeepLinkingSettings settings,
            final LtiAssignmentAndGradeServicesHelper.AssignmentAndGradeServicesCapabilities capabilities)
            throws RuntimeException {
        validateResourceLinkCanBeCreated(settings);
        validateScoreScope(capabilities);
    }

    /**
     * Validates that the given {@code settings} indicates that a Resource Link can be created.
     *
     * @param settings The {@link LtiDeepLinkingRequestHelper.DeepLinkingSettings} to be analyzed.
     */
    private static void validateResourceLinkCanBeCreated(
            final LtiDeepLinkingRequestHelper.DeepLinkingSettings settings) throws RuntimeException {
        if (!settings.getAcceptTypes().contains(LTI_RESOURCE_LINK)) {
            throw new LtiBadRequestException("Missing lti resource link in accept types");
        }
    }

    /**
     * Validates that the {@link LtiAssignmentAndGradeServicesHelper#SCORE_SCOPE} is present in the given
     * {@code capabilities} scope.
     *
     * @param capabilities The {@link LtiAssignmentAndGradeServicesHelper.AssignmentAndGradeServicesCapabilities}
     *                     to be analyzed.
     */
    private static void validateScoreScope(
            final LtiAssignmentAndGradeServicesHelper.AssignmentAndGradeServicesCapabilities capabilities)
            throws RuntimeException {
        if (!capabilities.getScopes().contains(LtiAssignmentAndGradeServicesHelper.SCORE_SCOPE)) {
            throw new LtiBadRequestException("Missing score scope");
        }
    }


    /**
     * Transforms the given {@code exam} into an {@link LtiResourceLink.Builder} instance
     * (i.e creates the {@link LtiResourceLink.Builder} instance and configures it with the information in the given
     * {@code exam}, without building the {@link LtiResourceLink} in order to allow further configuration).
     *
     * @param exam The {@link ExamData} with information to configure the {@link LtiResourceLink.Builder}.
     * @return The created {@link LtiResourceLink.Builder} instance.
     */
    private static LtiResourceLink.Builder adaptExamToLti(final Exam exam) {
        final var examId = Long.toString(exam.getId());
        final var lineItem = LineItem.builder().scoreMaximum(exam.getMaxScore()).resourceId(examId).build();

        return LtiResourceLink.builder().title(exam.getDescription()).lineItem(lineItem).custom(EXAM_ID_CUSTOM, examId);
    }

    /**
     * Maps the given {@code exam} into an {@link ExamData} instance.
     *
     * @param exam The {@link Exam} to be mapped.
     * @return The {@link ExamData} instance.
     */
    private static ExamData examExamToExamData(final Exam exam) {
        return new ExamData(
                exam.getId(),
                exam.getDescription(),
                exam.getStartingAt(),
                exam.getDuration(),
                exam.getMaxScore()
        );
    }

    /**
     * Maps an {@link ExamSelectedRequest.Image} into an {@link LtiResourceLink.Image}.
     *
     * @param image The {@link ExamSelectedRequest.Image} to be mapped.
     * @return The created {@link LtiResourceLink.Image}.
     */
    private static LtiResourceLink.Image mapImage(final ExamSelectedRequest.Image image) {
        return LtiResourceLink.Image.builder()
                .url(image.getUrl())
                .width(image.getWidth())
                .height(image.getHeight())
                .build()
                ;
    }

    /**
     * Extracts the {@link Map} of custom properties in the given {@code ltiMessage}.
     *
     * @param ltiMessage The LTI message to be analyzed.
     * @return The {@link Map} of custom properties.
     */
    private static Optional<Map<String, Object>> getCustomProperties(final Map<String, Object> ltiMessage) {
        return getMap(LtiConstants.LtiClaims.CUSTOM, ltiMessage);
    }

    /**
     * Retrieves the launch presentation {@link Map} from the given {@code ltiMessage}.
     *
     * @param ltiMessage The LTI message to be analyzed.
     * @return THe launch presentation {@link Map}.
     */
    private static Optional<Map<String, Object>> launchPresentation(final Map<String, Object> ltiMessage) {
        return getMap(LtiConstants.LtiClaims.LAUNCH_PRESENTATION, ltiMessage);
    }

    /**
     * Retrieves the {@link Map} for the given {@code claim} from the given {@code ltiMessage}.
     *
     * @param claim      The claim to be extracted.
     * @param ltiMessage The LTI message to be analyzed.
     * @return THe launch presentation {@link Map}.
     */
    private static Optional<Map<String, Object>> getMap(final String claim, final Map<String, Object> ltiMessage) {
        return Optional.ofNullable(ltiMessage.get(claim))
                .filter(Map.class::isInstance)
                .map(m -> (Map<?, ?>) m)
                .filter(m -> m.keySet().stream().allMatch(String.class::isInstance))
                .map(m -> m.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), Map.Entry::getValue)))
                ;
    }


    /**
     * A functional interface that defines a method to build an object from a
     * {@link ToolDeployment} and an LTI message.
     *
     * @param <C> The concrete type of object to be built
     */
    @FunctionalInterface
    private interface MessageHandler<C> {

        /**
         * Builds an object from a {@link ToolDeployment} and an LTI message.
         *
         * @param toolDeployment The {@link ToolDeployment} representing the integration exchanging the message.
         * @param ltiMessage     A {@link Map} representing the LTI message.
         * @return The built object.
         */
        C handle(final ToolDeployment toolDeployment, final Map<String, Object> ltiMessage);
    }
}
