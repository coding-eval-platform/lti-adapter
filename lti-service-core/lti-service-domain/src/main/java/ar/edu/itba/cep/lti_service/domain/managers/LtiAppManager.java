package ar.edu.itba.cep.lti_service.domain.managers;

import ar.edu.itba.cep.lti.*;
import ar.edu.itba.cep.lti_service.domain.helpers.*;
import ar.edu.itba.cep.lti_service.domain.helpers.LtiDeepLinkingResponseHelper.LtiResourceLink;
import ar.edu.itba.cep.lti_service.domain.helpers.LtiDeepLinkingResponseHelper.LtiResourceLink.LineItem;
import ar.edu.itba.cep.lti_service.external_cep_services.evaluations_service.EvaluationsService;
import ar.edu.itba.cep.lti_service.external_cep_services.evaluations_service.Exam;
import ar.edu.itba.cep.lti_service.models.admin.ToolDeployment;
import ar.edu.itba.cep.lti_service.repositories.ToolDeploymentRepository;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static ar.edu.itba.cep.lti_service.domain.helpers.LtiDeepLinkingRequestHelper.DeepLinkingContentType.LTI_RESOURCE_LINK;

/**
 * Manager in charge of providing services that allows LTI 1.3 interaction.
 */
@AllArgsConstructor
public class LtiAppManager implements LtiService {

    private static final String EXAM_ID_CUSTOM = "exam-id";

    private final ToolDeploymentRepository toolDeploymentRepository;
    private final LtiStateHelper ltiStateHelper;
    private final LtiMessageDeserializer ltiMessageDeserializer;
    private final LtiMessageValidator ltiMessageValidator;
    private final LtiDeepLinkingRequestHelper ltiDeepLinkingRequestHelper;
    private final ExamCreationStateHelper examCreationStateHelper;
    private final LtiMessageSerializer ltiMessageSerializer;
    private final LtiDeepLinkingResponseHelper ltiDeepLinkingResponseHelper;

    private final EvaluationsService evaluationsService;


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
        return this.handleAuthenticationResponse(authenticationResponse, this::createExamInitiation);
    }


    @Override
    public ExamSelectedResponse examSelected(final ExamSelectedRequest examSelectedRequest) {
        return evaluationsService.getExamById(examSelectedRequest.getExamId())
                .map(exam -> existingExam(exam, examSelectedRequest))
                .orElseGet(NonExistingExamSelectedResponse::getInstance)
                ;
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
                .orElseThrow(IllegalStateException::new) // TODO: define new exception?
                ;
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
    private ExamSelectionResponse createExamInitiation(final ToolDeployment toolDeployment, final Map<String, Object> ltiMessage) {

        // First get the deep linking settings, and validate that exams can be created.
        final var settings = ltiDeepLinkingRequestHelper.extractDeepLinkingSettings(ltiMessage);
        validateExamCanBeCreated(settings);


        // Then create the state that needs to be sent to the UA.
        final var examCreationStateData = ExamCreationStateHelper.ExamCreationStateData.create(
                settings.getReturnUrl(),
                settings.getData(),
                toolDeployment.getId(),
                UUID.randomUUID().toString()
        );
        final var state = examCreationStateHelper.encode(examCreationStateData);

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
        final var state = examCreationStateHelper.decode(request.getState());
        final var toolDeployment = toolDeploymentRepository
                .findById(state.getToolDeploymentId())
                .orElseThrow(IllegalStateException::new) // TODO: define new exception?
                ;
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


    // ================== Other helpers ===================

    /**
     * Validates that an exam can be created (using the Deep Linking settings)
     *
     * @param settings The {@link LtiDeepLinkingRequestHelper.DeepLinkingSettings}
     *                 that indicate if an exam can be created.
     * @throws RuntimeException If an exam cannot be created.
     */
    private static void validateExamCanBeCreated(final LtiDeepLinkingRequestHelper.DeepLinkingSettings settings)
            throws RuntimeException {
        if (!settings.getAcceptTypes().contains(LTI_RESOURCE_LINK)) {
            throw new RuntimeException(); // TODO: define new exception?
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
