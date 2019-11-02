package ar.edu.itba.cep.lti_service.spring_data.interfaces;

import ar.edu.itba.cep.lti_service.models.ExamTaking;
import ar.edu.itba.cep.lti_service.models.ToolDeployment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * A {@link CrudRepository} for {@link ToolDeployment}s.
 */
@Repository
public interface SpringDataExamTakingRepository extends CrudRepository<ExamTaking, UUID> {

    /**
     * Retrieves the {@link ExamTaking} with the given {@code examId} and {@code subject}.
     *
     * @param examId  The exam id.
     * @param subject The subject.
     * @return An {@link Optional} containing the matching {@link ExamTaking} if it exists, or empty otherwise.
     */
    Optional<ExamTaking> findByExamIdAndSubject(final long examId, final String subject);

    /**
     * Indicates whether an {@link ExamTaking} exists with the given{@code examId} and {@code subject}.
     *
     * @param examId  The exam id.
     * @param subject The subject.
     * @return {@code true} if there is a matching {@link ExamTaking}, or {@code false} otherwise.
     */
    boolean existsByExamIdAndSubject(final long examId, final String subject);
}
