package ar.edu.itba.cep.lti_service.external_cep_services.tokens_service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

/**
 * A Data Transfer Object that wraps token data, including its id, its expiration {@link Instant},
 * the {@link String} representation, and a refresh token.
 */
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
/* package */ class TokenDataDto {

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final UUID id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final String accessToken;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final String refreshToken;

    /**
     * Maps {@code this} DTO into a {@link TokenData} instance.
     *
     * @return The {@link TokenData} corresponding to {@code this} DTO.
     */
    /* package */ TokenData toTokenData() {
        return new TokenData(id, accessToken, refreshToken);
    }
}
