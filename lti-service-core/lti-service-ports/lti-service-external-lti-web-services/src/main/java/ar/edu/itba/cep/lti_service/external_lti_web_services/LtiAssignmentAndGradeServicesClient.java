package ar.edu.itba.cep.lti_service.external_lti_web_services;

import ar.edu.itba.cep.lti_service.models.ExamTaking;
import com.bellotapps.webapps_commons.exceptions.ExternalServiceException;

/**
 * A port out of the application that allows publishing scores in an LMS.
 */
public interface LtiAssignmentAndGradeServicesClient {

    /**
     * Publishes the given {@code score} for the given {@code examTaking}.
     *
     * @param examTaking The {@link ExamTaking} to which the {@code score} must be published.
     * @param score      The score to be assigned.
     * @throws ExternalServiceException If there is any issue when communicating with the authorization server.
     */
    void publishScore(final ExamTaking examTaking, final int score) throws ExternalServiceException;
}
