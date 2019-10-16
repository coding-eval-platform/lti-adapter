package ar.edu.itba.cep.lti_service.models.app;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.Optional;

/**
 * Represents an LTI authentication request.
 * This request is performed by the UA to an LMS
 * (through a redirect sent by this application after a login initiation request).
 * Check <a href=https://www.imsglobal.org/spec/security/v1p0/#openid_connect_launch_flow>IMS Security Framework,
 * section 5.1.1: OpenID Connect Launch Flow Overview</a> for more information.
 * This entity represents the following step:
 * <a href=https://www.imsglobal.org/spec/security/v1p0/#step-2-authentication-request>
 * IMS Security Framework, section 5.1.1.2: Step 2: Authentication Request</a>
 *
 * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#openid_connect_launch_flow>IMS Security Framework,
 * section 5.1.1: OpenID Connect Launch Flow Overview</a>.
 * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#step-2-authentication-request>
 * IMS Security Framework, section 5.1.1.2: Step 2: Authentication Request</a>.
 */
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
public class LtiAuthenticationRequest {

    // ================================
    // Constants
    // ================================

    /**
     * The "fixed" part of the authentication request {@link URI}. This part of the {@link URI} is always the same
     * (it contains fixed values).
     */
    private static final String FIXED_PART = "&prompt=none&scope=openid&response_type=id_token&response_mode=form_post";


    // ================================
    // Properties
    // ================================

    /**
     * The endpoint to which the request must be sent.
     */
    private final String endpoint;

    /**
     * The client id used to perform the LTI authentication request.
     */
    private final String clientId;
    /**
     * The login hint sent by the LMS platform in the login initiation request.
     */
    private final String loginHint;
    /**
     * The uri to which the user must be redirected at the end of the Open-ID connect flow.
     */
    private final String redirectUri;

    /**
     * The LTI message hint sent by the LMS platform in the login initiation request.
     */
    private final String ltiMessageHint;
    /**
     * An opaque value for the platform to maintain state between the LTI authentication request and the callback
     * (the access to the redirect uri), and to provide Cross-Site Request Forgery (CSRF) mitigation.
     */
    private final String state;

    /**
     * A nonce required by the LTI spec to associate a Client session with an ID Token, and to mitigate replay attacks.
     */
    private final String nonce;


    // ================================
    // Constructor
    // ================================

    /**
     * Constructor.
     *
     * @param endpoint       The endpoint to which the request must be sent.
     * @param clientId       The client id used to perform the LTI authentication request.
     * @param loginHint      The login hint sent by the LMS platform in the login initiation request.
     * @param redirectUri    The uri to which the user must be redirected at the end of the Open-ID connect flow.
     * @param nonce          A nonce required by the LTI spec to associate a Client session with an ID Token,
     *                       and to mitigate replay attacks.
     * @param ltiMessageHint The LTI message hint sent by the LMS platform in the login initiation request.
     * @param state          An opaque value for the platform to maintain state between the LTI authentication request
     *                       and the callback (the access to the redirect uri),
     *                       and to provide Cross-Site Request Forgery (CSRF) mitigation.
     * @throws IllegalArgumentException In case any value is not a valid one.
     */
    public LtiAuthenticationRequest(
            final String endpoint,
            final String clientId,
            final String loginHint,
            final String redirectUri,
            final String nonce,
            final String ltiMessageHint,
            final String state) throws IllegalArgumentException {
        assertEndpoint(endpoint);
        assertClientId(clientId);
        assertLoginHint(loginHint);
        assertRedirectUri(redirectUri);
        assertNonce(nonce);

        this.endpoint = endpoint;
        this.clientId = clientId;
        this.loginHint = loginHint;
        this.redirectUri = redirectUri;
        this.nonce = nonce;
        this.ltiMessageHint = ltiMessageHint;
        this.state = state;
    }


    // ================================
    // Getter
    // ================================

    /**
     * Creates the {@link URI} to which the user must be redirected to continue the authentication flow
     * (i.e the {@link URI} to be accessed to perform the authentication request)
     *
     * @return The created {@link URI}, based on properties of {@code this} instance.
     */
    public URI getUri() {
        final var uri = endpoint
                + "?client_id=" + clientId
                + "&login_hint=" + loginHint
                + "&redirect_uri=" + redirectUri
                + Optional.ofNullable(ltiMessageHint).map(str -> "&lti_message_hint=" + str).orElse("")
                + Optional.ofNullable(state).map(str -> "&state=" + str).orElse("")
                + "&nonce=" + nonce
                + FIXED_PART;

        return URI.create(uri);
    }


    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code endpoint} is valid.
     *
     * @param endpoint The endpoint to be checked.
     * @throws IllegalArgumentException In case the endpoint is not valid.
     */
    private static void assertEndpoint(final String endpoint) throws IllegalArgumentException {
        Assert.notNull(endpoint, "The endpoint must not be null");
    }

    /**
     * Asserts that the given {@code clientId} is valid.
     *
     * @param clientId The client id to be checked.
     * @throws IllegalArgumentException In case the client id is not valid.
     */
    private static void assertClientId(final String clientId) throws IllegalArgumentException {
        Assert.notNull(clientId, "The client id must not be null");
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
     * Asserts that the given {@code redirectUri} is valid.
     *
     * @param redirectUri The redirect uri to be checked.
     * @throws IllegalArgumentException In case the redirect uri is not valid.
     */
    private static void assertRedirectUri(final String redirectUri) throws IllegalArgumentException {
        Assert.notNull(redirectUri, "The redirect uri must not be null");
    }

    /**
     * Asserts that the given {@code nonce} is valid.
     *
     * @param nonce The nonce to be checked.
     * @throws IllegalArgumentException In case the nonce is not valid.
     */
    private static void assertNonce(final String nonce) throws IllegalArgumentException {
        Assert.notNull(nonce, "The nonce must not be null");
    }
}
