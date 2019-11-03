package ar.edu.itba.cep.lti_service.domain.managers;

import ar.edu.itba.cep.lti_service.models.ToolDeployment;
import ar.edu.itba.cep.lti_service.repositories.ToolDeploymentRepository;
import ar.edu.itba.cep.lti_service.services.LtiAdminService;
import com.bellotapps.webapps_commons.errors.UniqueViolationError;
import com.bellotapps.webapps_commons.exceptions.UniqueViolationException;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Manager in charge of providing services that allows configuring the LTI behaviour
 * (i.e allow registering {@link ToolDeployment}s).
 */
@Service
@AllArgsConstructor
public class LtiAdminManager implements LtiAdminService {

    /**
     * A {@link ToolDeploymentRepository}.
     */
    private final ToolDeploymentRepository toolDeploymentRepository;
//    /**
//     * A {@link KeyFactory} used to
//     */
//    private final KeyFactory keyFactory;


    // ================================================================================================================
    // Tool deployments
    // ================================================================================================================

    @Override
    public List<ToolDeployment> getAllToolDeployments() {
        final var deployments = toolDeploymentRepository.findAll();
        return StreamSupport.stream(deployments.spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public List<ToolDeployment> find(final String issuer) {
        return toolDeploymentRepository.find(issuer);
    }

    @Override
    public List<ToolDeployment> find(final String clientId, final String issuer) {
        return toolDeploymentRepository.find(clientId, issuer);
    }

    @Override
    public Optional<ToolDeployment> getToolDeploymentById(final UUID id) {
        return toolDeploymentRepository.findById(id);
    }

    @Override
    public Optional<ToolDeployment> find(final String deploymentId, final String clientId, final String issuer) {
        return toolDeploymentRepository.find(deploymentId, clientId, issuer);
    }

    @Override
    public ToolDeployment registerToolDeployment(
            final String deploymentId,
            final String clientId,
            final String issuer,
            final String oidcAuthenticationEndpoint,
            final String jwksEndpoint,
            final String privateKey,
            final SignatureAlgorithm signatureAlgorithm) throws IllegalArgumentException, UniqueViolationException {
        // First check if there is a tool deployment for the given deploymentId, clientId and issuer.
        if (toolDeploymentRepository.exists(deploymentId, clientId, issuer)) {
            throw new UniqueViolationException(List.of(TOOL_DEPLOYMENT_ALREADY_EXISTS));
        }

        // If not, create, save, and return it.
        final var toolDeployment = new ToolDeployment(
                deploymentId,
                clientId,
                issuer,
                oidcAuthenticationEndpoint,
                jwksEndpoint,
                privateKey,
                signatureAlgorithm
        );
        return toolDeploymentRepository.save(toolDeployment);
    }

    @Override
    public void unregisterToolDeployment(final UUID id) {
        if (toolDeploymentRepository.existsById(id)) {
            toolDeploymentRepository.deleteById(id);
        }
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * An {@link UniqueViolationError} that indicates that a {@link ToolDeployment} already exists
     * for with a given deployment id, client id and issuer.
     */
    private final static UniqueViolationError TOOL_DEPLOYMENT_ALREADY_EXISTS =
            new UniqueViolationError(
                    "A Tool Deployment already exists for a given deployment id, client id and issuer",
                    "deploymentId", "clientId", "issuer"
            );
}
