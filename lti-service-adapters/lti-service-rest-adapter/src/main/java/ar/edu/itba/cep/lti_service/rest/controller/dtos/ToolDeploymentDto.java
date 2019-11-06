package ar.edu.itba.cep.lti_service.rest.controller.dtos;

import ar.edu.itba.cep.lti_service.models.ToolDeployment;
import ar.edu.itba.cep.lti_service.rest.controller.validation.ValidPrivateKeyAndSignatureAlgorithm;
import ar.edu.itba.cep.lti_service.rest.controller.validation.ValidSignatureAlgorithm;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.IllegalValue;
import com.bellotapps.webapps_commons.errors.ConstraintViolationError.ErrorCausePayload.MissingValue;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Represents a tool deployment in an LTI platform.
 */
@Value
@ToString(exclude = {
        "privateKey",
        "signatureAlgorithm",
        "applicationKey",
        "applicationSecret",
})
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@ValidPrivateKeyAndSignatureAlgorithm(
        message = "The private-key/signature-algorithm pair is not valid",
        payload = IllegalValue.class
)
public class ToolDeploymentDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private final UUID id;
    @NotNull(message = "The Deployment id is missing.", payload = MissingValue.class)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private final String deploymentId;
    @NotNull(message = "The Client id is missing.", payload = MissingValue.class)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private final String clientId;
    @NotNull(message = "The Issuer is missing.", payload = MissingValue.class)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private final String issuer;
    @NotNull(message = "The OpenId Connect Authentication Endpoint is missing.", payload = MissingValue.class)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private final String oidcAuthenticationEndpoint;
    @NotNull(message = "The JWKS Endpoint is missing.", payload = MissingValue.class)
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private final String jwksEndpoint;
    @NotNull(message = "The Private Key is missing.", payload = MissingValue.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final String privateKey;
    @NotNull(message = "The Signature Algorithm is missing.", payload = MissingValue.class)
    @ValidSignatureAlgorithm(message = "The signature algorithm is not supported", payload = IllegalValue.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final SignatureAlgorithm signatureAlgorithm;
    @NotNull(message = "The application key is missing.", payload = MissingValue.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final String applicationKey;
    @NotNull(message = "The application secret is missing.", payload = MissingValue.class)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final String applicationSecret;


    /**
     * Builds a {@link ToolDeploymentDto} from the given {@link ToolDeployment}.
     *
     * @param toolDeployment The {@link ToolDeployment} to map.
     * @return The created {@link ToolDeploymentDto}.
     */
    public static ToolDeploymentDto fromModel(final ToolDeployment toolDeployment) {
        return new ToolDeploymentDto(
                toolDeployment.getId(),
                toolDeployment.getDeploymentId(),
                toolDeployment.getClientId(),
                toolDeployment.getIssuer(),
                toolDeployment.getOidcAuthenticationEndpoint(),
                toolDeployment.getJwksEndpoint(),
                toolDeployment.getPrivateKey(),
                toolDeployment.getSignatureAlgorithm(),
                toolDeployment.getApplicationKey(),
                toolDeployment.getApplicationSecret()
        );
    }
}
