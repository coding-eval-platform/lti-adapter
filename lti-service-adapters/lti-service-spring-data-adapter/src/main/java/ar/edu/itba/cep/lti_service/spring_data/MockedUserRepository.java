package ar.edu.itba.cep.lti_service.spring_data.config;

import ar.edu.itba.cep.lti_service.repositories.UserRepository;
import org.springframework.stereotype.Repository;

/**
 * A mocked {@link ar.edu.itba.cep.lti_service.models.User} repository used to boot the application.
 * Remove when Spring Data dependencies are added.
 */
@Repository
// TODO: remove this class as Spring Data will create all the needed repositories.
public class MockedUserRepository implements UserRepository {
}
