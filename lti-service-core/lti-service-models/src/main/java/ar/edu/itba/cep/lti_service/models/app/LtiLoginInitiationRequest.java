package ar.edu.itba.cep.lti_service.models.app;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

/**
 * Represents an LTI login initiation request.
 * This request is performed by the UA to this application
 * (through a redirect sent by the LMS after the user starts an LTI tool from the said LMS).
 * Check <a href=https://www.imsglobal.org/spec/security/v1p0/#openid_connect_launch_flow>IMS Security Framework,
 * section 5.1.1: OpenID Connect Launch Flow Overview</a> for more information.
 * This entity represents the following step:
 * <a href=https://www.imsglobal.org/spec/security/v1p0/#step-1-third-party-initiated-login>
 * IMS Security Framework, section 5.1.1.1: Step 1: Third-party Initiated Login</a>.
 *
 * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#openid_connect_launch_flow>IMS Security Framework,
 * section 5.1.1: OpenID Connect Launch Flow Overview</a>.
 * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#step-1-third-party-initiated-login>
 * IMS Security Framework, section 5.1.1.1: Step 1: Third-party Initiated Login</a>.
 */
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
public class LtiLoginInitiationRequest {

    /**
     * The issuing authority (identifies the LMS).
     */
    private final String issuer;
    /**
     * Hint needed by the LMS.
     */
    private final String loginHint;
    /**
     * The actual end-point that should be executed at the end of the authentication flow.
     */
    private final String targetLinkUri;
    /**
     * An optional field used alongside the {@code loginHint} by the LMS
     * to carry information about the received LTI message.
     */
    private final String ltiMessageHint;
    /**
     * An optional field used to identify a specific {@link ar.edu.itba.cep.lti_service.models.admin.ToolDeployment}.
     */
    private final String deploymentId;
    /**
     * An optional field used to identify a specific {@link ar.edu.itba.cep.lti_service.models.admin.ToolDeployment}.
     */
    private final String clientId;


    /**
     * @param issuer         The issuing authority (identifies the LMS).
     * @param loginHint      Hint needed by the LMS. It is replicated "as is" in the {@link LtiAuthenticationRequest}
     *                       being created.
     * @param targetLinkUri  The actual end-point that should be executed at the end of the authentication flow.
     *                       It is replicated "as is" in the {@link LtiAuthenticationRequest} being created,
     *                       in the "redirect uri" property.
     * @param ltiMessageHint An optional parameter used alongside the {@code loginHint} by the LMS to carry
     *                       information about the received LTI message.
     * @param deploymentId   An optional parameter used to identify a specific
     *                       {@link ar.edu.itba.cep.lti_service.models.admin.ToolDeployment}.
     * @param clientId       An optional parameter used to identify a specific
     *                       {@link ar.edu.itba.cep.lti_service.models.admin.ToolDeployment}.
     * @throws IllegalArgumentException In case any value is not a valid one.
     */
    public LtiLoginInitiationRequest(
            final String issuer,
            final String loginHint,
            final String targetLinkUri,
            final String ltiMessageHint,
            final String deploymentId,
            final String clientId) {
        assertIssuer(issuer);
        assertLoginHint(loginHint);
        assertTargetLinkUri(targetLinkUri);

        this.issuer = issuer;
        this.loginHint = loginHint;
        this.targetLinkUri = targetLinkUri;
        this.ltiMessageHint = ltiMessageHint;
        this.deploymentId = deploymentId;
        this.clientId = clientId;
    }


    /**
     * Asserts that the given {@code issuer} is valid.
     *
     * @param issuer The issuer to be checked.
     * @throws IllegalArgumentException In case the issuer is not valid.
     */
    private static void assertIssuer(final String issuer) throws IllegalArgumentException {
        Assert.notNull(issuer, "The issuer must not be null");
    }

    /**
     * Asserts that the given {@code loginHint} is valid.
     *
     * @param loginHint The login hint to be checked.
     * @throws IllegalArgumentException In case the login hint is not valid.
     */
    private static void assertLoginHint(final String loginHint) throws IllegalArgumentException {
        Assert.notNull(loginHint, "The LTI login hint must not be null");
    }

    /**
     * Asserts that the given {@code targetLinkUri} is valid.
     *
     * @param targetLinkUri The target link uri to be checked.
     * @throws IllegalArgumentException In case the target link uri is not valid.
     */
    private static void assertTargetLinkUri(final String targetLinkUri) throws IllegalArgumentException {
        Assert.notNull(targetLinkUri, "The target link uri must not be null");
    }
}
