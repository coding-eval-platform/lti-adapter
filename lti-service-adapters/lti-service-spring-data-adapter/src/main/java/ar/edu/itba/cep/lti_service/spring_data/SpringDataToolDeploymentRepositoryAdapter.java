package ar.edu.itba.cep.lti_service.spring_data;

import ar.edu.itba.cep.lti_service.models.ToolDeployment;
import ar.edu.itba.cep.lti_service.repositories.ToolDeploymentRepository;
import ar.edu.itba.cep.lti_service.spring_data.interfaces.SpringDataToolDeploymentRepository;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.repositories.BasicRepositoryAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * A mock implementation of a {@link ToolDeploymentRepository}, created in order to boot the application.
 */
@Repository
@AllArgsConstructor
public class SpringDataToolDeploymentRepositoryAdapter
        implements ToolDeploymentRepository, BasicRepositoryAdapter<ToolDeployment, UUID> {


    /**
     * A {@link SpringDataToolDeploymentRepository} to which all operations are delegated.
     */
    private final SpringDataToolDeploymentRepository repository;


    // ================================================================================================================
    // RepositoryAdapter
    // ================================================================================================================

    @Override
    public SpringDataToolDeploymentRepository getCrudRepository() {
        return repository;
    }


    // ================================================================================================================
    // ToolDeploymentRepository specific methods
    // ================================================================================================================

    @Override
    public List<ToolDeployment> find(final String issuer) {
        return repository.findByIssuer(issuer);
    }

    @Override
    public List<ToolDeployment> find(final String clientId, final String issuer) {
        return repository.findByClientIdAndIssuer(clientId, issuer);
    }

    @Override
    public Optional<ToolDeployment> find(final String deploymentId, final String clientId, final String issuer) {
        return repository.findByDeploymentIdAndClientIdAndIssuer(deploymentId, clientId, issuer);
    }

    @Override
    public boolean exists(final String deploymentId, final String clientId, final String issuer) {
        return repository.existsByDeploymentIdAndClientIdAndIssuer(deploymentId, clientId, issuer);
    }
}
