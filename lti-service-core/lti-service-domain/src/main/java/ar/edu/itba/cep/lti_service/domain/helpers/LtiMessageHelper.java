package ar.edu.itba.cep.lti_service.domain.helpers;

import ar.edu.itba.cep.lti_service.models.admin.ToolDeployment;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Component in charge of aiding with LTI messages tasks.
 */
@Component
public class LtiMessageHelper {

    /**
     * The LTI version supported by this tool.
     */
    private static final String LTI_VERSION = "1.3.0";


    /**
     * Parses the given {@code idToken} into a valid LTI message.
     *
     * @param idToken        The id token to be parsed into an LTI message.
     * @param toolDeployment A {@link ToolDeployment} needed to validate the {@code idToken}.
     *                       Will be used to check both signature and the content of the message.
     * @param nonce          The nonce that must be matched.
     * @return The parsed LTI message, in the form of a {@link Map}.
     * @throws RuntimeException If the ... // TODO: complete
     * @apiNote This method always returns a valid LTI message.
     */
    public Map<String, Object> parseMessage(
            final String idToken,
            final ToolDeployment toolDeployment,
            final String nonce)
            throws RuntimeException {
        Assert.hasText(idToken, "The id token must have text");
        Assert.notNull(toolDeployment, "The tool deployment must not be null");
        try {
            final var ltiMessage = Jwts.parser()
                    .setSigningKeyResolver(ToolDeploymentJwksSigningKeyResolver.create(toolDeployment))
                    .parseClaimsJws(idToken)
                    .getBody();
            validateLtiMessage(toolDeployment, nonce, ltiMessage);
            return ltiMessage;
        } catch (final JwtException e) {
            throw new RuntimeException("The id token could not be parsed", e); // TODO define new exception
        }
    }


    /**
     * Validates the content of the given {@code ltiMessage} (just content, not signature stuff).
     *
     * @param toolDeployment The {@link ToolDeployment} that must be matched with stuff in the {@code ltiMessage}.
     * @param nonce          The nonce that must be matched.
     * @param ltiMessage     The LTI message to be validated.
     * @throws RuntimeException If the {@code ltiMessage} is not valid.
     * @implNote This method only verifies the issuer, the audience, the authorized party and the nonce Claims.
     * The signature, and the alg and exp Claims are verified when the id token is parsed.
     * The iat Claim is ignored.
     * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#authentication-response-validation>
     * Authentication Response Validation</a>
     */
    private static void validateLtiMessage(
            final ToolDeployment toolDeployment,
            final String nonce,
            final Map<String, Object> ltiMessage) throws RuntimeException {
        validateVersion(ltiMessage); // First validate the version (another version might have other validations).
        validateIssuer(toolDeployment.getIssuer(), ltiMessage);
        validateClientId(toolDeployment.getClientId(), ltiMessage);
        validateDeploymentId(toolDeployment.getDeploymentId(), ltiMessage);
        validateNonce(nonce, ltiMessage);
    }


    /**
     * Validates the version of the given {@code ltiMessage} (must match {@link #LTI_VERSION}.
     *
     * @param ltiMessage The LTI message to be validated.
     * @throws RuntimeException If the {@code ltiMessage} is not valid.
     */
    private static void validateVersion(final Map<String, Object> ltiMessage) throws RuntimeException {
        Optional.ofNullable(ltiMessage.get(LtiClaims.VERSION))
                .filter(LTI_VERSION::equals)
                .orElseThrow(RuntimeException::new) // TODO: define new exception
        ;
    }

    /**
     * Validates the issuer Claim in the given {@code ltiMessage}.
     *
     * @param issuer     The issuer to be matched.
     * @param ltiMessage The LTI message to be validated.
     * @throws RuntimeException If the issuer Claim in the {@code ltiMessage} is not valid
     *                          (is missing or it does not match the given {@code issuer}).
     * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#authentication-response-validation>
     * IMS Security Framework, section 5.1.1.2: Step 2: Authentication Request</a>
     */
    private static void validateIssuer(final String issuer, final Map<String, Object> ltiMessage)
            throws RuntimeException {
        Assert.notNull(issuer, "The issuer must not be null");
        // The issuer for the platform MUST exactly match the value of the iss (Issuer) Claim
        Optional.ofNullable(ltiMessage.get(LtiClaims.ISSUER))
                .filter(issuer::equals)
                .orElseThrow(RuntimeException::new) // TODO: define new exception
        ;
    }

    /**
     * Validates the audience and authorized party Claims in the given {@code ltiMessage}.
     *
     * @param clientId   The client id that must be present in the {@code ltiMessage} in the audience or in the
     *                   authorized party Claim.
     * @param ltiMessage The LTI message to be validated.
     * @throws RuntimeException If the audience Claim is not present,
     *                          or if it does not contain the given {@code clientId},
     *                          or if it is a {@link Collection} and the authorized party Claim does not match
     *                          the given {@code clientId}.
     * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#authentication-response-validation>
     * Authentication Response Validation, step 3, 4 and 5</a>
     */
    private static void validateClientId(final String clientId, final Map<String, Object> ltiMessage)
            throws RuntimeException {
        Assert.notNull(clientId, "The client id must not be null");
        // The aud (audience) Claim MUST contains the client id value registered with the Issuer
        // identified by the iss (Issuer) Claim. The aud Claim MAY contain an array with more than one element.
        final var audience = Optional.ofNullable(ltiMessage.get(LtiClaims.AUDIENCE))
                .filter(val -> val instanceof String || val instanceof Collection)
                .filter(val -> val instanceof String ? clientId.equals(val) : ((Collection) val).contains(clientId))
                .orElseThrow(RuntimeException::new);
        // In case more than one element is present in the audience claim...
        // If there are multiple audiences, the azp Claim must be present and match the client id
        if (audience instanceof Collection) {
            Optional.ofNullable(ltiMessage.get(LtiClaims.AUTHORIZED_PARTY))
                    .filter(clientId::equals) // TODO: define new exception
                    .orElseThrow(RuntimeException::new) // TODO: define new exception
            ;
        }
    }

    /**
     * Validates the deployment id Claim in the  given {@code ltiMessage}.
     *
     * @param deploymentId The deployment id that must be matched.
     * @param ltiMessage   The LTI message to be validated.
     * @throws RuntimeException If the deployment id in the {@code ltiMessage}
     *                          does not match the given {@code deploymentId}.
     */
    private static void validateDeploymentId(final String deploymentId, final Map<String, Object> ltiMessage)
            throws RuntimeException {
        Assert.notNull(deploymentId, "The deployment id must not be null");
        Optional.ofNullable(ltiMessage.get(LtiClaims.DEPLOYMENT_ID))
                .filter(deploymentId::equals)
                .orElseThrow(RuntimeException::new) // TODO: define new exception
        ;
    }

    /**
     * Validates the nonce Claim in the given {@code ltiMessage}.
     *
     * @param nonce      The nonce to be matched.
     * @param ltiMessage The LTI message to be validated.
     * @throws RuntimeException If the nonce Claim in the {@code ltiMessage} is not valid
     *                          (is missing or it does not match the given {@code nonce}).
     * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#authentication-response-validation>
     * Authentication Response Validation, step 9</a>
     */
    private static void validateNonce(final String nonce, final Map<String, Object> ltiMessage) throws RuntimeException {
        Assert.notNull(nonce, "The nonce must not be null");
        // The nonce Claim must be present.
        // The received nonce must be unique to avoid replay attacks.
        // This is achieved by using UUID as a nonce when creating the LtiAuthenticationRequest
        // (note that the nonce is part of the state sent in the said request, which is then signed with a private key).
        Optional.ofNullable(ltiMessage.get(LtiClaims.NONCE))
                .filter(nonce::equals)
                .orElseThrow(RuntimeException::new) // TODO: define new exception
        ;
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
