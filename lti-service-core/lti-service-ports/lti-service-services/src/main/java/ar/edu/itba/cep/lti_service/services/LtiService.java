package ar.edu.itba.cep.lti_service.services;

import ar.edu.itba.cep.lti.LtiExamScoringService;
import ar.edu.itba.cep.lti.LtiExamSelectionService;
import ar.edu.itba.cep.lti.LtiExamTakingService;
import ar.edu.itba.cep.lti.LtiLoginService;

/**
 * Convenient interface that centralizes all of the LTI services defined in the commons library.
 */
public interface LtiService
        extends LtiLoginService, LtiExamSelectionService, LtiExamTakingService, LtiExamScoringService {
}
