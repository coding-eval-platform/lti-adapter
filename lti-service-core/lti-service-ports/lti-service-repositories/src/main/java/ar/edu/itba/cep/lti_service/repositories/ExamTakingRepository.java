package ar.edu.itba.cep.lti_service.repositories;

import ar.edu.itba.cep.lti_service.models.ExamTaking;
import com.bellotapps.webapps_commons.persistence.repository_utils.repositories.BasicRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * A port out of the application that allows {@link ExamTaking} persistence.
 */
public interface ExamTakingRepository extends BasicRepository<ExamTaking, UUID> {

    /**
     * Retrieves the {@link ExamTaking} with the given {@code examId} and {@code subject}.
     *
     * @param examId  The exam id.
     * @param subject The subject.
     * @return An {@link Optional} containing the matching {@link ExamTaking} if it exists, or empty otherwise.
     */
    Optional<ExamTaking> get(final long examId, final String subject);
}
