package ar.edu.itba.cep.lti_service.external_lti_web_services.oauth2;

import ar.edu.itba.cep.lti_service.external_lti_web_services.config.RestTemplateExternalLtiWebServicesConfig;
import ar.edu.itba.cep.lti_service.models.ToolDeployment;
import ar.edu.itba.cep.security.KeyHelper;
import com.bellotapps.webapps_commons.exceptions.ExternalServiceException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * Concrete implementation of {@link OAuth2Client}.
 */
@Component
class RestTemplateOAuth2Client implements OAuth2Client {


    private static final String CLIENT_CREDENTIALS_GRANT_TYPE = "client_credentials";
    private static final String CLIENT_ASSERTION_TYPE_JWT = "urn:ietf:params:oauth:client-assertion-type:jwt-bearer";
    private static final String ASSERTION_JWT_TYPE_HEADER_FIELD = "typ";
    private static final String ASSERTION_JWT_TYPE_HEADER_VALUE = "JWT";

    /**
     * The {@link RestTemplate} used to communicate with the authorization server.
     */
    private final RestTemplate restTemplate;
    /**
     * The {@link Duration} of an assertion JWT.
     */
    private final Duration assertionJwtDuration;

    /**
     * Constructor.
     *
     * @param restTemplate The {@link RestTemplate} used to communicate with the authorization server.
     * @param properties   The {@link RestTemplateExternalLtiWebServicesConfig.LtiWebServicesProperties} instance with
     *                     properties needed to configure this client.
     */
    @Autowired
    public RestTemplateOAuth2Client(
            final RestTemplate restTemplate,
            final RestTemplateExternalLtiWebServicesConfig.LtiWebServicesProperties properties) {
        this.restTemplate = restTemplate;
        this.assertionJwtDuration = Duration.ofMinutes(properties.getAssertionJwtDuration());
    }

    @Override
    public String getAccessToken(final ToolDeployment toolDeployment, final List<String> scopes)
            throws ExternalServiceException {
        Assert.notNull(toolDeployment, "The ToolDeployment must not be null");
        Assert.notEmpty(scopes, "The scopes list must not be null or empty");
        final var response = sendRequest(toolDeployment, scopes);
        return response.getAccessToken();
    }


    /**
     * Performs the OAuth2 request.
     *
     * @param toolDeployment The {@link ToolDeployment} with data needed to get the token
     *                       (e.g application key, application secret, authentication endpoint, etc.).
     * @param scopes         The scopes that must be enabled by the token.
     * @return An {@link OAuth2Response} with the retrieved data.
     * @throws ExternalServiceException If there is any issue when communicating with the authorization server.
     */
    private OAuth2Response sendRequest(final ToolDeployment toolDeployment, final List<String> scopes)
            throws ExternalServiceException {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        final var body = OAuth2Request.builder()
                .grantType(CLIENT_CREDENTIALS_GRANT_TYPE)
                .scopes(scopes)
                .clientAssertionType(CLIENT_ASSERTION_TYPE_JWT)
                .clientAssertion(buildJwt(toolDeployment))
                .build()
                .asMap();
        try {
            return restTemplate.postForObject(
                    toolDeployment.getOidcAuthenticationEndpoint(),
                    new HttpEntity<>(body, headers),
                    OAuth2Response.class
            );
        } catch (final Throwable e) {
            throw new ExternalServiceException(
                    toolDeployment.getIssuer(),
                    "Could not communicate with the LMS",
                    e);
        }
    }


    /**
     * Builds an assertion JWT using the given {@code toolDeployment}'s data.
     *
     * @param toolDeployment The {@link ToolDeployment} from where data will be taken.
     * @return The created assertion JWT.
     */
    private String buildJwt(final ToolDeployment toolDeployment) {
        final var privateKey = privateKey(toolDeployment.getPrivateKey(), toolDeployment.getSignatureAlgorithm());
        final var now = Instant.now();
        return Jwts.builder()
                .setHeaderParam(ASSERTION_JWT_TYPE_HEADER_FIELD, ASSERTION_JWT_TYPE_HEADER_VALUE)
                .setIssuer(toolDeployment.getClientId())
                .setSubject(toolDeployment.getClientId())
                .setAudience(toolDeployment.getIssuer())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(assertionJwtDuration)))
                .setId(UUID.randomUUID().toString())
                .signWith(privateKey, toolDeployment.getSignatureAlgorithm())
                .compact()
                ;
    }

    /**
     * Transforms the given {@code privateKey} (i.e in base64 encoded form) into a {@link PrivateKey}.
     *
     * @param privateKey         The base64 encoded private key.
     * @param signatureAlgorithm The {@link SignatureAlgorithm} (i.e used to get a {@link KeyFactory} instance).
     * @return The corresponding {@link PrivateKey}.
     */
    private PrivateKey privateKey(final String privateKey, final SignatureAlgorithm signatureAlgorithm) {
        try {
            final var keyFactory = KeyFactory.getInstance(signatureAlgorithm.getFamilyName());
            return KeyHelper.generateKey(
                    keyFactory, privateKey, PKCS8EncodedKeySpec::new, KeyFactory::generatePrivate
            );
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalStateException("ToolDeployment with invalid Private Key");
        }
    }


    /**
     * The request to be sent to obtain an access token as part of the OAuth2 protocol.
     */
    @Builder(builderClassName = "Builder")
    private static final class OAuth2Request {
        private static final String GRANT_TYPE_FIELD = "grant_type";
        private static final String SCOPE_FIELD = "scope";
        private static final String CLIENT_ASSERTION_TYPE_FIELD = "client_assertion_type";
        private static final String CLIENT_ASSERTION_FIELD = "client_assertion";


        private final String grantType;
        private final List<String> scopes;
        private final String clientAssertionType;
        private final String clientAssertion;


        /**
         * Converts {@code this} object into a {@link MultiValueMap}
         * (i.e in order to be sent as a WWW-URL-Encoded form).
         *
         * @return The {@link MultiValueMap} corresponding to {@code this} request.
         */
        private MultiValueMap<String, String> asMap() {
            return new LinkedMultiValueMap<>(
                    Map.of(
                            GRANT_TYPE_FIELD, List.of(grantType),
                            SCOPE_FIELD, List.of(String.join(" ", scopes)),
                            CLIENT_ASSERTION_TYPE_FIELD, List.of(clientAssertionType),
                            CLIENT_ASSERTION_FIELD, List.of(clientAssertion)
                    )
            );
        }
    }

    /**
     * The response returned by the OAuth2 server.
     */
    @Value
    @NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
    private static final class OAuth2Response {
        @JsonProperty(value = "access_token", access = JsonProperty.Access.WRITE_ONLY)
        private final String accessToken;
        @JsonProperty(value = "token_type", access = JsonProperty.Access.WRITE_ONLY)
        private final String tokenType;
        @JsonProperty(value = "expires_in", access = JsonProperty.Access.WRITE_ONLY)
        private final int ttl;
        @JsonProperty(value = "scope", access = JsonProperty.Access.WRITE_ONLY)
        @JsonDeserialize(using = ScopesDeserializer.class)
        private final List<String> scopes;
    }

    /**
     * A {@link com.fasterxml.jackson.databind.JsonDeserializer} that is able to get the scopes as a {@link List}
     * of {@link String}s from a single {@link String} delimited by a space.
     */
    private static final class ScopesDeserializer extends StdDeserializer<List<String>> {

        private ScopesDeserializer() {
            super(List.class);
        }

        @Override
        public List<String> deserialize(final JsonParser parser, final DeserializationContext context)
                throws IOException {
            final var scope = parser.getText();
            return Arrays.asList(scope.split(" "));
        }
    }
}
