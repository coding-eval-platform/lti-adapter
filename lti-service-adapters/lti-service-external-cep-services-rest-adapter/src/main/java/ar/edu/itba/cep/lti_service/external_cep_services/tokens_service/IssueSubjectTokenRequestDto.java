package ar.edu.itba.cep.lti_service.external_cep_services.tokens_service;

import ar.edu.itba.cep.roles.Role;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.MissingValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * A Data Transfer Object with the needed information to issue a new token.
 */
@Value
/* package */ class IssueSubjectTokenRequestDto {

    /**
     * The username.
     */
    @JsonProperty(value = "subject", access = JsonProperty.Access.READ_ONLY)
    private final String subject;
    /**
     * The password.
     */
    @JsonProperty(value = "roles", access = JsonProperty.Access.READ_ONLY)
    private final Set<@NotNull(message = "Null role.", payload = MissingValue.class) Role> roles;
}
