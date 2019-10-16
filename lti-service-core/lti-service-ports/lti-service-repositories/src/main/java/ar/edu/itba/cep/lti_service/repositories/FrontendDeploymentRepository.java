package ar.edu.itba.cep.lti_service.repositories;

import ar.edu.itba.cep.lti_service.models.admin.FrontendDeployment;
import com.bellotapps.webapps_commons.persistence.repository_utils.repositories.BasicRepository;

import java.util.UUID;

/**
 * A port out of the application that allows {@link FrontendDeployment} persistence.
 */
public interface FrontendDeploymentRepository extends BasicRepository<FrontendDeployment, UUID> {
}
