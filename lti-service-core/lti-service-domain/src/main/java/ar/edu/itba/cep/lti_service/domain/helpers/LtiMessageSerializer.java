package ar.edu.itba.cep.lti_service.domain.helpers;

import ar.edu.itba.cep.lti_service.models.ToolDeployment;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Component in charge of serializing LTI messages.
 */
@Component
public class LtiMessageSerializer {

    /**
     * The LTI version supported by this tool.
     */
    private static final String LTI_VERSION = "1.3.0";


    public String serialize(final Map<String, Object> ltiMessage, final ToolDeployment toolDeployment) {
        replaceInstant(ltiMessage, LtiConstants.LtiClaims.EXPIRATION);
        replaceInstant(ltiMessage, LtiConstants.LtiClaims.ISSUED_AT);


        return Jwts.builder()
//                .setHeaderParam("kid", ltiMessage.get(LtiConstants.LtiClaims.ISSUER))
                .addClaims(ltiMessage)
                .signWith(toolDeployment.getPrivateKey(), toolDeployment.getSignatureAlgorithm())
                .compact()
                ;
    }


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
