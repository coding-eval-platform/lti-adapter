package ar.edu.itba.cep.lti_service.models.admin;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * Represents a tool deployment in an LTI platform.
 */
@Getter
@NoArgsConstructor(force = true)
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(of = "id", doNotUseGetters = true)
public class ToolDeployment {

    /**
     * The tool deployment id (this is an internal id).
     */
    private final UUID id;
    /**
     * The deployment id (given by the platform).
     */
    private final String deploymentId;
    /**
     * The id given to the tool by the issuer.
     */
    private final String clientId;
    /**
     * The issuing authority
     */
    private final String issuer;
    /**
     * Endpoint to which the user agent is redirected after a login initiation request.
     */
    private final String oidcAuthenticationEndpoint;
    /**
     * Endpoint at which the platform's public keys can be found.
     */
    private final String jwksEndpoint;


    /**
     * Constructor.
     *
     * @param deploymentId               The deployment id (given by the platform).
     * @param clientId                   The id given to the tool by the issuer.
     * @param issuer                     The issuing authority.
     * @param oidcAuthenticationEndpoint Endpoint to which the user agent is redirected after a login initiation request.
     * @param jwksEndpoint               Endpoint at which the platform's public keys can be found.
     * @throws IllegalArgumentException In case any value is not a valid one.
     */
    public ToolDeployment(
            final String deploymentId,
            final String clientId,
            final String issuer,
            final String oidcAuthenticationEndpoint,
            final String jwksEndpoint) throws IllegalArgumentException {
        assertDeploymentId(deploymentId);
        assertClientId(clientId);
        assertIssuer(issuer);
        assertOidcAuthenticationEndpoint(oidcAuthenticationEndpoint);
        assertJwksEndpoint(jwksEndpoint);

        this.id = null;
        this.deploymentId = deploymentId;
        this.clientId = clientId;
        this.issuer = issuer;
        this.oidcAuthenticationEndpoint = oidcAuthenticationEndpoint;
        this.jwksEndpoint = jwksEndpoint;
    }


    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code deploymentId} is valid.
     *
     * @param deploymentId The deployment id to be checked.
     * @throws IllegalArgumentException In case the deployment id is not valid.
     */
    private static void assertDeploymentId(final String deploymentId) throws IllegalArgumentException {
        Assert.notNull(deploymentId, "The deployment id must not be null");
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
     * Asserts that the given {@code issuer} is valid.
     *
     * @param issuer The issuer to be checked.
     * @throws IllegalArgumentException In case the issuer is not valid.
     */
    private static void assertIssuer(final String issuer) throws IllegalArgumentException {
        Assert.notNull(issuer, "The issuer must not be null");
    }

    /**
     * Asserts that the given {@code oidcAuthenticationEndpoint} is valid.
     *
     * @param oidcAuthenticationEndpoint The oidc authentication endpoint to be checked.
     * @throws IllegalArgumentException In case the oidc authentication endpoint is not valid.
     */
    private static void assertOidcAuthenticationEndpoint(final String oidcAuthenticationEndpoint)
            throws IllegalArgumentException {
        Assert.notNull(oidcAuthenticationEndpoint, "The oidc authentication endpoint must not be null");
    }

    /**
     * Asserts that the given {@code jwksEndpoint} is valid.
     *
     * @param jwksEndpoint The jwks endpoint to be checked.
     * @throws IllegalArgumentException In case the jwks endpoint is not valid.
     */
    private static void assertJwksEndpoint(final String jwksEndpoint) throws IllegalArgumentException {
        Assert.notNull(jwksEndpoint, "The jwks endpoint must not be null");
    }
}
