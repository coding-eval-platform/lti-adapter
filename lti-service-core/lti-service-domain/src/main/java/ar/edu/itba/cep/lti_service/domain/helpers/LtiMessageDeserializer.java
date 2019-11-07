package ar.edu.itba.cep.lti_service.domain.helpers;

import ar.edu.itba.cep.lti.LtiAuthenticationException;
import ar.edu.itba.cep.lti_service.models.ToolDeployment;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyConverter;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URL;
import java.security.Key;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Component in charge of deserializing LTI messages.
 */
@Component
public class LtiMessageDeserializer {

    /**
     * Parses the given {@code idToken} into an LTI message.
     *
     * @param idToken        The id token to be parsed into an LTI message.
     * @param toolDeployment A {@link ToolDeployment} needed to validate the {@code idToken}.
     *                       Needed to retrieve the public key used to verify the message.
     * @return The parsed LTI message, in the form of a {@link Map}.
     * @throws LtiAuthenticationException If the id token cannot be decoded.
     */
    public Map<String, Object> deserialize(final String idToken, final ToolDeployment toolDeployment)
            throws RuntimeException {
        Assert.hasText(idToken, "The id token must have text");
        Assert.notNull(toolDeployment, "The tool deployment must not be null");
        try {
            return Jwts.parser()
                    .setSigningKeyResolver(ToolDeploymentJwksSigningKeyResolver.create(toolDeployment))
                    .parseClaimsJws(idToken)
                    .getBody();
        } catch (final JwtException e) {
            throw new LtiAuthenticationException("The id token could not be parsed", e);
        }
    }


    /**
     * An extension of {@link SigningKeyResolverAdapter} that retrieves the public {@link Key} corresponding
     * to a platform according to a given {@link ToolDeployment}.
     */
    @AllArgsConstructor(staticName = "create")
    private static final class ToolDeploymentJwksSigningKeyResolver extends SigningKeyResolverAdapter {

        /**
         * The {@link ToolDeployment} whose platform's public key must be retrieved.
         */
        private final ToolDeployment toolDeployment;


        @Override
        public Key resolveSigningKey(JwsHeader header, Claims claims) {
            final var set = getJwks(toolDeployment).orElseThrow(IllegalStateException::new);
            final var jwk = set.getKeyByKeyId(header.getKeyId());
            return KeyConverter.toJavaKeys(List.of(jwk)).get(0);
        }


        /**
         * Retrieves the {@link JWKSet} corresponding to the given {@code toolDeployment}.
         *
         * @param toolDeployment The {@link ToolDeployment} whose {@link JWKSet} must be retrieved.
         * @return An {@link Optional} containing the {@link JWKSet} if it could be retrieved, or empty otherwise.
         */
        private static Optional<JWKSet> getJwks(final ToolDeployment toolDeployment) {
            try {
                final var url = new URL(toolDeployment.getJwksEndpoint());
                final var set = JWKSet.load(url);
                return Optional.of(set);
            } catch (final IOException | ParseException e) {
                return Optional.empty();
            }
        }
    }
}
