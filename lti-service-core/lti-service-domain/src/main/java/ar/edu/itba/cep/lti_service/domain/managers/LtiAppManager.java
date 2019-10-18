package ar.edu.itba.cep.lti_service.domain.managers;

import ar.edu.itba.cep.lti_service.domain.helpers.ExamCreationStateHelper;
import ar.edu.itba.cep.lti_service.domain.helpers.LtiDeepLinkingHelper;
import ar.edu.itba.cep.lti_service.domain.helpers.LtiMessageHelper;
import ar.edu.itba.cep.lti_service.domain.helpers.LtiStateHelper;
import ar.edu.itba.cep.lti_service.models.admin.FrontendDeployment;
import ar.edu.itba.cep.lti_service.models.admin.ToolDeployment;
import ar.edu.itba.cep.lti_service.models.app.LtiAuthenticationRequest;
import ar.edu.itba.cep.lti_service.models.app.LtiAuthenticationResponse;
import ar.edu.itba.cep.lti_service.models.app.LtiContent;
import ar.edu.itba.cep.lti_service.models.app.LtiLoginInitiationRequest;
import ar.edu.itba.cep.lti_service.repositories.FrontendDeploymentRepository;
import ar.edu.itba.cep.lti_service.repositories.ToolDeploymentRepository;
import ar.edu.itba.cep.lti_service.services.LtiAppService;
import com.bellotapps.webapps_commons.exceptions.NoSuchEntityException;
import com.bellotapps.webapps_commons.exceptions.NotImplementedException;
import lombok.AllArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static ar.edu.itba.cep.lti_service.domain.helpers.LtiDeepLinkingHelper.DeepLinkingContentType.LTI_RESOURCE_LINK;

/**
 * Manager in charge of providing services that allows LTI 1.3 interaction.
 */
@AllArgsConstructor
public class LtiAppManager implements LtiAppService {

    /**
     * a {@link ToolDeploymentRepository} used to retrieve {@link ToolDeployment}s according the received LTI message.
     */
    private final ToolDeploymentRepository toolDeploymentRepository;
    /**
     * A {@link FrontendDeploymentRepository} used to retrieve the {@link FrontendDeployment}, in order to send
     * the LTI content to the consumer.
     */
    private final FrontendDeploymentRepository frontendDeploymentRepository;
    /**
     * An {@link LtiStateHelper} used to encode/decode the state sent/received in the LTI messages.
     */
    private final LtiStateHelper ltiStateHelper;
    /**
     * An {@link LtiMessageHelper} that helps with the task of handling LTI messages.
     */
    private final LtiMessageHelper ltiMessageHelper;
    /**
     * An {@link LtiDeepLinkingHelper} used to handle Deep Linking messages.
     */
    private final LtiDeepLinkingHelper ltiDeepLinkingHelper;
    /**
     * An {@link ExamCreationStateHelper} that helps with the task of "creating exams".
     */
    private final ExamCreationStateHelper examCreationStateHelper;


    @Override
    public LtiAuthenticationRequest loginInitiation(final LtiLoginInitiationRequest loginInitiationRequest) {
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
        return new LtiAuthenticationRequest(
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
    public LtiContent createExamInitiation(final LtiAuthenticationResponse response) {
        return handleLtiAuthenticationResponse(response, this::createExamInitiation);
    }


    @Override
    public void createExamFinalization() {
        throw new NotImplementedException("Not yet implemented");
    }

    @Override
    public void takeExam() {
        throw new NotImplementedException("Not yet implemented");
    }


    /**
     * Performs the exam creation initiation step of the Deep Linking flow.
     *
     * @param toolDeployment The {@link ToolDeployment} representing the integration exchanging the message.
     * @param ltiMessage     A {@link Map} representing the LTI message.
     * @return The {@link LtiContent} representing the content to be shown in the UA in order to select the exam
     * to be created.
     */
    private LtiContent createExamInitiation(final ToolDeployment toolDeployment, final Map<String, Object> ltiMessage) {

        // First get the deep linking settings, and validate that exams can be created.
        final var settings = ltiDeepLinkingHelper.extractDeepLinkingSettings(ltiMessage);
        validateExamCanBeCreated(settings);


        // Then create the state that needs to be sent to the UA.
        final var examCreationStateData = ExamCreationStateHelper.ExamCreationStateData.create(
                settings.getReturnUrl(),
                settings.getData(),
                toolDeployment.getId(),
                UUID.randomUUID().toString()
        );
        final var state = examCreationStateHelper.encode(examCreationStateData);

        // Retrieve the frontend.
        final var frontendDeploymentIterator = frontendDeploymentRepository.findAll().iterator();
        Assert.state(frontendDeploymentIterator.hasNext(), "No frontend deployment");
        final var frontendDeployment = frontendDeploymentIterator.next();

        // And expand the template with the state.
        final var examCreationUrlTemplate = frontendDeployment.getExamCreationUrlTemplate();
        final var url = StringSubstitutor.replace(examCreationUrlTemplate, examCreationTemplateValuesMap(state));

        // Return the content.
        return new LtiContent(url);
    }


    /**
     * Validates that an exam can be created (using the Deep Linking settings)
     *
     * @param deepLinkingSettings The {@link LtiDeepLinkingHelper.DeepLinkingSettings}
     *                            that indicate if an exam can be created.
     * @throws RuntimeException If an exam cannot be created.
     */
    private static void validateExamCanBeCreated(final LtiDeepLinkingHelper.DeepLinkingSettings deepLinkingSettings)
            throws RuntimeException {
        if (!deepLinkingSettings.getAcceptTypes().contains(LTI_RESOURCE_LINK)) {
            throw new RuntimeException(); // TODO: define new exception?
        }
    }


    /**
     * Handles the given {@code response}, continuing the flow with the given {@code andThen} {@link LtiMessageHandler}.
     *
     * @param response The {@link LtiAuthenticationResponse} to be handled.
     * @param andThen  The {@link LtiMessageHandler} used to perform the actual handling operation.
     * @return The {@link LtiContent} resulting from the handling.
     */
    private LtiContent handleLtiAuthenticationResponse(
            final LtiAuthenticationResponse response,
            final LtiMessageHandler andThen) {

        final var stateData = ltiStateHelper.decode(response.getState());
        final var toolDeployment = toolDeploymentRepository
                .findById(stateData.getToolDeploymentId())
                .orElseThrow(IllegalStateException::new) // TODO: define new exception?
                ;
        final var ltiMessage = ltiMessageHelper.parseMessage(response.getIdToken(), toolDeployment, stateData.getNonce());
        return andThen.handle(toolDeployment, ltiMessage);
    }

    /**
     * A functional interface that defines a method to build an {@link LtiContent} from a
     * {@link ToolDeployment} and an LTI message.
     */
    @FunctionalInterface
    private interface LtiMessageHandler {

        /**
         * Builds an {@link LtiContent} from a {@link ToolDeployment} and an LTI message.
         *
         * @param toolDeployment The {@link ToolDeployment} representing the integration exchanging the message.
         * @param ltiMessage     A {@link Map} representing the LTI message.
         * @return The {@link LtiContent}.
         */
        LtiContent handle(final ToolDeployment toolDeployment, final Map<String, Object> ltiMessage);
    }

    /**
     * Creates the value {@link Map} needed to interpolate the "Exam Creation" url template
     * in a {@link FrontendDeployment} with a {@link StringSubstitutor}.
     *
     * @param state The state to be used in the interpolation.
     * @return The created {@link Map}
     */
    private static Map<String, String> examCreationTemplateValuesMap(final String state) {
        return Map.of(removeVariableSeparator(FrontendDeployment.STATE_VARIABLE), state);
    }

    /**
     * Removes the variable separators from the given {@code str}. This method is used to populate a value {@link Map}
     * used by a {@link StringSubstitutor} to resolve a template.
     *
     * @param str The {@link String} to which the separators will be removed.
     * @return The same {@link String}, but with the variable separators removed.
     */
    private static String removeVariableSeparator(final String str) {
        return str.replace(StringSubstitutor.DEFAULT_VAR_START, "").replace(StringSubstitutor.DEFAULT_VAR_END, "");
    }
}
