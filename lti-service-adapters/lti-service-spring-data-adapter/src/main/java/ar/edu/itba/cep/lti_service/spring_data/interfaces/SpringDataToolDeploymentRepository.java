package ar.edu.itba.cep.lti_service.spring_data.interfaces;

import ar.edu.itba.cep.lti_service.models.ToolDeployment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A {@link CrudRepository} for {@link ToolDeployment}s.
 */
@Repository
public interface SpringDataToolDeploymentRepository extends CrudRepository<ToolDeployment, UUID> {

    /**
     * Retrieves all the {@link ToolDeployment}s matching the given {@code issuer}.
     *
     * @param issuer The issuer.
     * @return A {@link List} of {@link ToolDeployment}s matching the given {@code issuer}.
     */
    List<ToolDeployment> findByIssuer(final String issuer);

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
    List<ToolDeployment> findByClientIdAndIssuer(final String clientId, final String issuer);

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
    Optional<ToolDeployment> findByDeploymentIdAndClientIdAndIssuer(
            final String deploymentId,
            final String clientId,
            final String issuer
    );

    /**
     * Indicates whether a {@link ToolDeployment} exists
     * with the given {@code deploymentId}, {@code clientId} and {@code issuer}
     *
     * @param deploymentId The deployment id.
     * @param clientId     The client id.
     * @param issuer       The issuer.
     * @return {@code true} if there is a matching {@link ToolDeployment}, or {@code false} otherwise.
     */
    boolean existsByDeploymentIdAndClientIdAndIssuer(
            final String deploymentId,
            final String clientId,
            final String issuer
    );
}
