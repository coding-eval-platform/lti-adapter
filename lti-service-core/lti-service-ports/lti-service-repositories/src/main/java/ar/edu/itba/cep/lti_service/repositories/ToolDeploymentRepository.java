package ar.edu.itba.cep.lti_service.repositories;

import ar.edu.itba.cep.lti_service.models.admin.ToolDeployment;
import com.bellotapps.webapps_commons.persistence.repository_utils.repositories.BasicRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A port out of the application that allows {@link ToolDeployment} persistence.
 */
public interface ToolDeploymentRepository extends BasicRepository<ToolDeployment, UUID> {

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
     * Indicates whether a {@link ToolDeploymentRepository} exists
     * with the given {@code deploymentId}, {@code clientId} and {@code issuer}
     *
     * @param deploymentId The deployment id.
     * @param clientId     The client id.
     * @param issuer       The issuer.
     * @return {@code true} if there is a matching {@link ToolDeployment}, or {@code false} otherwise.
     */
    boolean exists(final String deploymentId, final String clientId, final String issuer);
}
