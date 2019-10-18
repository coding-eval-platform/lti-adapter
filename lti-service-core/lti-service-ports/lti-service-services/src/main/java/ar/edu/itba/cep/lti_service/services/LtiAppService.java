package ar.edu.itba.cep.lti_service.services;

import ar.edu.itba.cep.lti_service.models.app.LtiAuthenticationRequest;
import ar.edu.itba.cep.lti_service.models.app.LtiAuthenticationResponse;
import ar.edu.itba.cep.lti_service.models.app.LtiContent;
import ar.edu.itba.cep.lti_service.models.app.LtiLoginInitiationRequest;

/**
 * A port into the application that allows LTI 1.3 interaction.
 */
public interface LtiAppService {

    /**
     * Creates an {@link LtiAuthenticationRequest} from the given {@link LtiLoginInitiationRequest}.
     *
     * @param loginInitiationRequest The {@link LtiLoginInitiationRequest} from where the data needed to create
     *                               the {@link LtiAuthenticationRequest} is taken.
     * @return The created {@link LtiAuthenticationRequest}.
     * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#openid_connect_launch_flow>IMS Security Framework,
     * section 5.1.1: OpenID Connect Launch Flow Overview</a>.
     * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#step-1-third-party-initiated-login>
     * IMS Security Framework, section 5.1.1.1: Step 1: Third-party Initiated Login</a>.
     * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#step-2-authentication-request>
     * IMS Security Framework, section 5.1.1.2: Step 2: Authentication Request</a>
     */
    LtiAuthenticationRequest loginInitiation(final LtiLoginInitiationRequest loginInitiationRequest);

    /**
     * Performs the exam creation initiation step of the Deep Linking flow.
     *
     * @param ltiAuthenticationResponse The received {@link LtiAuthenticationResponse}.
     * @return The {@link LtiContent} representing the content to be shown in the UA in order to select the exam
     * to be created.
     * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#openid_connect_launch_flow>IMS Security Framework,
     * section 5.1.1: OpenID Connect Launch Flow Overview</a>.
     * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#step-3-authentication-response>
     * IMS Security Framework, section 5.1.1.3: Step 3: Authentication Response</a>
     * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#step-4-resource-is-displayed>
     * IMS Security Framework, section 5.1.1.4: Step 4: Resource is displayed</a>
     */
    LtiContent createExamInitiation(final LtiAuthenticationResponse ltiAuthenticationResponse);


    void createExamFinalization(); // TODO: define better

    void takeExam(); // TODO: define better
}

