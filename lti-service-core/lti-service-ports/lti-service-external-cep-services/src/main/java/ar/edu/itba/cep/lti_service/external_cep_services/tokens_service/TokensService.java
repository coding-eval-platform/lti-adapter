package ar.edu.itba.cep.lti_service.external_cep_services.tokens_service;

import ar.edu.itba.cep.roles.Role;
import com.bellotapps.webapps_commons.exceptions.ExternalServiceException;

import java.util.Optional;
import java.util.Set;

/**
 * A port out of the application that allows sending requests to the tokens service.
 */
public interface TokensService {

    /**
     * Creates a token for the given {@code subject}, with the given {@code roles}.
     * Retrieves the {@link TokenData} with the given {@code id}.
     *
     * @param subject The subject to which the token will be created.
     * @param roles   The {@link Role}s to be assigned.
     * @return An {@link Optional} holding the {@link TokenData} instance with the needed information if it could be
     * retrieved from the service, or empty otherwise.
     * @throws ExternalServiceException If there is any problem when communicating with the service.
     */
    Optional<TokenData> tokenFor(final String subject, final Set<Role> roles) throws ExternalServiceException;
}
