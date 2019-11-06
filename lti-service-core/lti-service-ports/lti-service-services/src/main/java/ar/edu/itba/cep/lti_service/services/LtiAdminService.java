package ar.edu.itba.cep.lti_service.services;

import ar.edu.itba.cep.lti_service.models.ToolDeployment;
import com.bellotapps.webapps_commons.exceptions.UniqueViolationException;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A port into the application that provides LTI services.
 */
public interface LtiAdminService {

    // ================================================================================================================
    // Tool deployments
    // ================================================================================================================

    /**
     * Retrieves all the {@link ToolDeployment}s.
     *
     * @return A {@link List} containing all the {@link ToolDeployment}s.
     */
    List<ToolDeployment> getAllToolDeployments();

    /**
     * Retrieves all the {@link ToolDeployment}s matching the given {@code issuer}.
     *
     * @param issuer The issuer.
     * @return A {@link List} of {@link ToolDeployment}s matching the given {@code issuer}.
     */
    List<ToolDeployment> find(final String issuer);

    /**
     * Retrieves all the {@link ToolDeployment}s matching the given {@code clientId} and {@code issuer}.
     * This might be a singleton {@link List}, depending of the tenancy model
     * (i.e for a multi-tenant model, given a client id and an issuer, a tool can be deployed several times;
     * for a single-tenant model, a tool is deployed once with its client id within an issuer).
     *
     * @param clientId The client id.
     * @param issuer   The issuer.
     * @return A {@link List} of {@link ToolDeployment}s matching the given {@code clientId} and {@code issuer}.
     */
    List<ToolDeployment> find(final String clientId, final String issuer);

    /**
     * Retrieves the {@link ToolDeployment} with the given {@code id}.
     *
     * @param id The id of the {@link ToolDeployment} to be retrieved.
     * @return An {@link Optional} containing the {@link ToolDeployment} with the given {@code id} if it exists,
     * or empty otherwise.
     */
    Optional<ToolDeployment> getToolDeploymentById(final UUID id);

    /**
     * Retrieves the {@link ToolDeployment}
     * matching the given {@code deploymentId}, {@code clientId} and {@code issuer}. For these three properties, only
     * once {@link ToolDeployment} exists.
     *
     * @param deploymentId The deployment id.
     * @param clientId     The client id.
     * @param issuer       The issuer.
     * @return An {@link Optional} containing the {@link ToolDeployment} matching the given arguments if it exists,
     * or empty otherwise.
     */
    Optional<ToolDeployment> find(final String deploymentId, final String clientId, final String issuer);

    /**
     * Creates a {@link ToolDeployment}.
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
     * @return The created {@link ToolDeployment}.
     * @throws IllegalArgumentException In case any value is not a valid one.
     * @throws UniqueViolationException If a {@link ToolDeployment} already exists for the given
     *                                  {@code deploymentId}, {@code clientId} and {@code issuer}.
     */
    ToolDeployment registerToolDeployment(
            final String deploymentId,
            final String clientId,
            final String issuer,
            final String oidcAuthenticationEndpoint,
            final String jwksEndpoint,
            final String privateKey,
            final SignatureAlgorithm signatureAlgorithm,
            final String applicationKey,
            final String applicationSecret) throws IllegalArgumentException, UniqueViolationException;

    /**
     * Removes the {@link ToolDeployment} with the given {@code id}
     *
     * @param id The id of the {@link ToolDeployment} to be removed.
     * @apiNote This is an idempotent operation.
     */
    void unregisterToolDeployment(final UUID id);
}
