package ar.edu.itba.cep.lti_service.external_cep_services.evaluations_service;

import com.bellotapps.webapps_commons.exceptions.ExternalServiceException;

import java.util.Optional;

/**
 * A port out of the application that allows sending requests to the evaluations service.
 */
public interface EvaluationsService {

    /**
     * Retrieves the {@link Exam} with the given {@code id}.
     *
     * @param id The id of the {@link Exam} to be retrieved.
     * @return An {@link Optional} containing the {@link Exam} if it exists, or empty otherwise.
     */
    Optional<Exam> getExamById(final long id) throws ExternalServiceException;
}
