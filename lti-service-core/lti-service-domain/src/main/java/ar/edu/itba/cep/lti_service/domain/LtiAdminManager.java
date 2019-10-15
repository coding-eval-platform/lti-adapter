package ar.edu.itba.cep.lti_service.domain;

import ar.edu.itba.cep.lti_service.models.admin.FrontendDeployment;
import ar.edu.itba.cep.lti_service.models.admin.ToolDeployment;
import ar.edu.itba.cep.lti_service.services.LtiAdminService;
import com.bellotapps.webapps_commons.exceptions.NotImplementedException;
import com.bellotapps.webapps_commons.exceptions.UniqueViolationException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Manager in charge of providing services that allows configuring the LTI behaviour
 * (i.e allow registering tool deployments and the frontend).
 */
public class LtiAdminManager implements LtiAdminService {

    @Override
    public List<ToolDeployment> getAllToolDeployments() {
        throw new NotImplementedException();
    }

    @Override
    public List<ToolDeployment> find(final String issuer) {
        throw new NotImplementedException();
    }

    @Override
    public List<ToolDeployment> find(final String clientId, final String issuer) {
        throw new NotImplementedException();
    }

    @Override
    public Optional<ToolDeployment> getToolDeploymentById(final UUID id) {
        throw new NotImplementedException();
    }

    @Override
    public Optional<ToolDeployment> find(final String deploymentId, final String clientId, final String issuer) {
        throw new NotImplementedException();
    }

    @Override
    public ToolDeployment registerToolDeployment(
            final String deploymentId,
            final String clientId,
            final String issuer,
            final String oidcAuthenticationEndpoint,
            final String jwksEndpoint) throws IllegalArgumentException {
        throw new NotImplementedException();
    }

    @Override
    public void unregisterToolDeployment(final UUID id) {
        throw new NotImplementedException();
    }

    @Override
    public FrontendDeployment registerFrontend(
            final String examCreationUrl,
            final String examTakingUrlTemplate) throws IllegalArgumentException, UniqueViolationException {
        throw new NotImplementedException();
    }

    @Override
    public void unregisterFrontend(final UUID id) {
        throw new NotImplementedException();
    }
}
