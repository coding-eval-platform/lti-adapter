package ar.edu.itba.cep.lti_service.models;

import ar.edu.itba.cep.security.KeyHelper;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.*;
import org.springframework.util.Assert;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.UUID;

/**
 * Represents a tool deployment in an LTI platform.
 */
@Getter
@ToString(doNotUseGetters = true)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
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
     * The private key needed to sign messages sent to the platform (base64 encoded).
     */
    private final String privateKey;
    /**
     * The {@link SignatureAlgorithm}.
     */
    private final SignatureAlgorithm signatureAlgorithm;
    /**
     * The tool deployment key (used for OAuth2 Client Credentials grant type request).
     */
    private final String applicationKey;
    /**
     * The tool deployment secret (used for OAuth2 Client Credentials grant type request).
     */
    private final String applicationSecret;


    /**
     * Constructor.
     *
     * @param deploymentId               The deployment id (given by the platform).
     * @param clientId                   The id given to the tool by the issuer.
     * @param issuer                     The issuing authority.
     * @param oidcAuthenticationEndpoint Endpoint to which the user agent is redirected after a login initiation request.
     * @param jwksEndpoint               Endpoint at which the platform's public keys can be found.
     * @param privateKey                 The private key needed to sign messages sent to the platform (base64 encoded).
     * @param signatureAlgorithm         The {@link SignatureAlgorithm}.
     * @param applicationKey             The tool deployment key
     *                                   (used for OAuth2 Client Credentials grant type request).
     * @param applicationSecret          The tool deployment secret
     *                                   (used for OAuth2 Client Credentials grant type request).
     * @throws IllegalArgumentException In case any value is not a valid one.
     */
    public ToolDeployment(
            final String deploymentId,
            final String clientId,
            final String issuer,
            final String oidcAuthenticationEndpoint,
            final String jwksEndpoint,
            final String privateKey,
            final SignatureAlgorithm signatureAlgorithm,
            final String applicationKey,
            final String applicationSecret) throws IllegalArgumentException {
        assertDeploymentId(deploymentId);
        assertClientId(clientId);
        assertIssuer(issuer);
        assertOidcAuthenticationEndpoint(oidcAuthenticationEndpoint);
        assertJwksEndpoint(jwksEndpoint);
        assertPrivateKey(privateKey);
        assertSignatureAlgorithm(signatureAlgorithm);
        assertPrivateKeyAndAlgorithm(privateKey, signatureAlgorithm);
        assertApplicationKey(applicationKey);
        assertApplicationSecret(applicationSecret);

        this.id = null;
        this.deploymentId = deploymentId;
        this.clientId = clientId;
        this.issuer = issuer;
        this.oidcAuthenticationEndpoint = oidcAuthenticationEndpoint;
        this.jwksEndpoint = jwksEndpoint;
        this.privateKey = privateKey;
        this.signatureAlgorithm = signatureAlgorithm;
        this.applicationKey = applicationKey;
        this.applicationSecret = applicationSecret;
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

    /**
     * Asserts that the given {@code privateKey} is valid.
     *
     * @param privateKey The private key to be checked.
     * @throws IllegalArgumentException In case the private key is not valid.
     */
    private static void assertPrivateKey(final String privateKey) throws IllegalArgumentException {
        Assert.notNull(privateKey, "The private key must not be null");
    }

    /**
     * Asserts that the given {@code signatureAlgorithm} is valid.
     *
     * @param signatureAlgorithm The {@link SignatureAlgorithm} to be checked.
     * @throws IllegalArgumentException In case the signatureAlgorithm is not valid.
     */
    private static void assertSignatureAlgorithm(final SignatureAlgorithm signatureAlgorithm)
            throws IllegalArgumentException {
        Assert.notNull(signatureAlgorithm, "The signatureAlgorithm must not be null");
    }

    /**
     * Asserts that the given {@code applicationKey} is valid.
     *
     * @param applicationKey The application key to be checked.
     * @throws IllegalArgumentException In case the application key is not valid.
     */
    private static void assertApplicationKey(final String applicationKey) throws IllegalArgumentException {
        Assert.notNull(applicationKey, "The application key must not be null");
    }

    /**
     * Asserts that the given {@code applicationSecret} is valid.
     *
     * @param applicationSecret The application secret to be checked.
     * @throws IllegalArgumentException In case the application secret is not valid.
     */
    private static void assertApplicationSecret(final String applicationSecret) throws IllegalArgumentException {
        Assert.notNull(applicationSecret, "The application secret must not be null");
    }

    /**
     * Asserts that both the {@code privateKey} and {@code algorithm} can work together.
     *
     * @param privateKey The private key to be checked.
     * @param algorithm  The {@link SignatureAlgorithm} to be checked.
     * @throws IllegalArgumentException If they are not compatible.
     * @throws IllegalStateException    If a {@link KeyFactory} cannot be instantiated for the given {@code algorithm}.
     */
    public static void assertPrivateKeyAndAlgorithm(final String privateKey, final SignatureAlgorithm algorithm)
            throws IllegalArgumentException, IllegalStateException {
        try {
            final var keyFactory = KeyFactory.getInstance(algorithm.getFamilyName());
            KeyHelper.generateKey(
                    keyFactory, privateKey, PKCS8EncodedKeySpec::new, KeyFactory::generatePrivate
            );
        } catch (final KeyHelper.InvalidKeyException e) {
            throw new IllegalArgumentException("The given private key is invalid");
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("Cannot create a KeyFactory for algorithm " + algorithm);
        }
    }
}
