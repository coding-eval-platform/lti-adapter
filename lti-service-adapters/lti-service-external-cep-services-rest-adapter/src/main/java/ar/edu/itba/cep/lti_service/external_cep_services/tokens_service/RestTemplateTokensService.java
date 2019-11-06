package ar.edu.itba.cep.lti_service.external_cep_services.tokens_service;

import ar.edu.itba.cep.lti_service.external_cep_services.Constants;
import ar.edu.itba.cep.lti_service.external_cep_services.config.RestExternalCepServicesConfig;
import ar.edu.itba.cep.roles.Role;
import com.bellotapps.webapps_commons.exceptions.ExternalServiceException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;
import java.util.Set;

/**
 * A port out of the application that allows sending requests to the evaluations service.
 */
@Component
public class RestTemplateTokensService implements TokensService {

    private static final String TOKENS_PATH = "/tokens";

    /**
     * The {@link RestTemplate} used to communicate with the evaluations service.
     */
    private final RestTemplate restTemplate;
    /**
     * The {@link URI} that has to be accessed in order to create the internal tokens.
     */
    private final URI internalTokensUri;


    /**
     * Constructor.
     *
     * @param loadBalancedRestTemplate The {@link RestTemplate} used to communicate with the evaluations service.
     * @param properties               An {@link RestExternalCepServicesConfig.EvaluationsServiceProperties} instance used to get
     *                                 the evaluations service's base url.
     */
    public RestTemplateTokensService(
            final RestTemplate loadBalancedRestTemplate,
            final RestExternalCepServicesConfig.TokensServiceProperties properties) {
        Assert.notNull(properties, "The properties instance must not be null");
        Assert.hasText(properties.getBaseUrl(), "The evaluations service's base url must not be blank");
        this.restTemplate = loadBalancedRestTemplate;
        this.internalTokensUri = UriComponentsBuilder.fromUriString(properties.getBaseUrl())
                .path(Constants.INTERNAL_PATH)
                .path(TOKENS_PATH)
                .build()
                .toUri()
        ;
    }

    @Override
    public Optional<TokenData> tokenFor(final String subject, final Set<Role> roles) throws ExternalServiceException {
        final var requestDto = new IssueSubjectTokenRequestDto(subject, roles);
        try {
            return Optional.ofNullable(restTemplate.postForObject(internalTokensUri, requestDto, TokenDataDto.class))
                    .map(TokenDataDto::toTokenData)
                    ;
        } catch (final RestClientException e) {
            throw new ExternalServiceException(
                    "tokens-service",
                    "Unexpected error when communicating with the tokens service (part of the users service)",
                    e
            );
        }
    }
}
