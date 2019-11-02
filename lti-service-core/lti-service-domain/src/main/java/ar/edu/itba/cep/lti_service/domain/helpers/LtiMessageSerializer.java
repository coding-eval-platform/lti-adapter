package ar.edu.itba.cep.lti_service.domain.helpers;

import ar.edu.itba.cep.lti_service.models.ToolDeployment;
import ar.edu.itba.cep.security.KeyHelper;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Component in charge of serializing LTI messages.
 */
@Component
public class LtiMessageSerializer {


    /**
     * Serializes the given {@code ltiMessage} into a JWT, using the key registered in the given {@code toolDeployment}.
     *
     * @param ltiMessage     The LTI Message to be serialized.
     * @param toolDeployment The {@link ToolDeployment} representing the integration between this tool and the LMS.
     * @return The serialized LTI Message.
     */
    public String serialize(final Map<String, Object> ltiMessage, final ToolDeployment toolDeployment) {
        replaceInstant(ltiMessage, LtiConstants.LtiClaims.EXPIRATION);
        replaceInstant(ltiMessage, LtiConstants.LtiClaims.ISSUED_AT);

        final var key = privateKey(toolDeployment);
        return Jwts.builder()
                .addClaims(ltiMessage)
                .signWith(key, toolDeployment.getSignatureAlgorithm())
                .compact()
                ;
    }


    /**
     * Transforms the given {@code encoded} {@link String} into a {@link PrivateKey}.
     *
     * @param toolDeployment The {@link ToolDeployment} representing the integration between this tool and the LMS.
     *                       The base64 encoded key and the signature algorithm will be taken from here.
     * @return Tthe corresponding {@link PrivateKey}.
     */
    private PrivateKey privateKey(final ToolDeployment toolDeployment) {
        try {
            final var keyFactory = KeyFactory.getInstance(toolDeployment.getSignatureAlgorithm().getFamilyName());
            return KeyHelper.generateKey(
                    keyFactory, toolDeployment.getPrivateKey(), PKCS8EncodedKeySpec::new, KeyFactory::generatePrivate
            );
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("ToolDeployment with invalid Private Key");
        }
    }


    /**
     * Replaces the given {claim} in the given {@code ltiMessage}, from an {@link Instant} into its epoch second format.
     *
     * @param ltiMessage The LTI Message to be processed.
     * @param claim      The claim in the {@code ltiMessage} to be processed.
     */
    private static void replaceInstant(final Map<String, Object> ltiMessage, final String claim) {
        Optional.ofNullable(ltiMessage.remove(claim))
                .filter(Instant.class::isInstance)
                .map(Instant.class::cast)
                .map(Instant::getEpochSecond)
                .ifPresentOrElse(
                        val -> ltiMessage.put(claim, val),
                        () -> {
                            throw new RuntimeException();
                        }
                );
    }
}
