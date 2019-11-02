package ar.edu.itba.cep.lti_service.spring_data;

import ar.edu.itba.cep.lti_service.models.ExamTaking;
import ar.edu.itba.cep.lti_service.repositories.ExamTakingRepository;
import ar.edu.itba.cep.lti_service.spring_data.interfaces.SpringDataExamTakingRepository;
import com.bellotapps.webapps_commons.persistence.spring_data.repository_utils_adapters.repositories.BasicRepositoryAdapter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * A mock implementation of a {@link ExamTakingRepository}, created in order to boot the application.
 */
@Repository
@AllArgsConstructor
public class SpringDataExamTakingRepositoryAdapter
        implements ExamTakingRepository, BasicRepositoryAdapter<ExamTaking, UUID> {

    /**
     * A {@link SpringDataExamTakingRepository} to which all operations are delegated.
     */
    private final SpringDataExamTakingRepository repository;


    // ================================================================================================================
    // RepositoryAdapter
    // ================================================================================================================

    @Override
    public SpringDataExamTakingRepository getCrudRepository() {
        return repository;
    }


    // ================================================================================================================
    // ToolDeploymentRepository specific methods
    // ================================================================================================================

    @Override
    public Optional<ExamTaking> get(final long examId, final String subject) {
        return repository.findByExamIdAndSubject(examId, subject);
    }

    @Override
    public boolean exists(final long examId, final String subject) {
        return repository.existsByExamIdAndSubject(examId, subject);
    }
}
