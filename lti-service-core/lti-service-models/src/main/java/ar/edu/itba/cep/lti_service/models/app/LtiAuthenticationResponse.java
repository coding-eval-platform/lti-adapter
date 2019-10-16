package ar.edu.itba.cep.lti_service.models.app;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

/**
 * Represents an LTI authentication request.
 * This request is performed by the UA to an LMS
 * (through a redirect sent by this application after a login initiation request).
 * Check <a href=https://www.imsglobal.org/spec/security/v1p0/#openid_connect_launch_flow>IMS Security Framework,
 * section 5.1.1: OpenID Connect Launch Flow Overview</a> for more information.
 * This entity represents the following step:
 * <a href=https://www.imsglobal.org/spec/security/v1p0/#step-3-authentication-response>
 * IMS Security Framework, section 5.1.1.3: Step 3: Authentication Response</a>
 *
 * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#openid_connect_launch_flow>IMS Security Framework,
 * section 5.1.1: OpenID Connect Launch Flow Overview</a>.
 * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#step-3-authentication-response>
 * IMS Security Framework, section 5.1.1.3: Step 3: Authentication Response</a>
 */
@Getter
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
public class LtiAuthenticationResponse {

    /**
     * The ID token that was sent by the LMS platform.
     */
    private final String idToken;
    /**
     * The state that was sent in the authentication request.
     */
    private final String state;


    // ================================
    // Constructor
    // ================================

    /**
     * Constructor.
     *
     * @param idToken The ID token that was sent by the LMS platform.
     * @param state   The state that was sent in the authentication request.
     * @throws IllegalArgumentException In case any value is not a valid one.
     */
    public LtiAuthenticationResponse(final String idToken, final String state) throws IllegalArgumentException {
        assertIdToken(idToken);
        assertState(state);

        this.idToken = idToken;
        this.state = state;
    }


    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code idToken} is valid.
     *
     * @param idToken The ID Token to be checked.
     * @throws IllegalArgumentException In case the ID Token is not valid.
     */
    private static void assertIdToken(final String idToken) throws IllegalArgumentException {
        Assert.notNull(idToken, "The ID Token must not be null");
    }

    /**
     * Asserts that the given {@code state} is valid.
     *
     * @param state The state to be checked.
     * @throws IllegalArgumentException In case the state is not valid.
     */
    private static void assertState(final String state) throws IllegalArgumentException {
        Assert.notNull(state, "The state must not be null");
    }
}
