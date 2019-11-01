package ar.edu.itba.cep.lti_service.external_cep_services.tokens_service;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

/**
 * A Data Transfer Object that wraps token data, including its id, its expiration {@link Instant},
 * the {@link String} representation, and a refresh token.
 */
@Value
@EqualsAndHashCode(of = "id", doNotUseGetters = true)
public class TokenData {

    /**
     * The token's id.
     */
    private final UUID id;
    /**
     * The access token.
     */
    private final String accessToken;
    /**
     * A token to be used to refresh the access token.
     */
    private final String refreshToken;
}
