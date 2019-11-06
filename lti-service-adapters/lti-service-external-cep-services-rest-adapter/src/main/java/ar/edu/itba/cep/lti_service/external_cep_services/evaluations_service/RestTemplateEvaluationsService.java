package ar.edu.itba.cep.lti_service.external_cep_services.evaluations_service;

import ar.edu.itba.cep.lti_service.external_cep_services.Constants;
import ar.edu.itba.cep.lti_service.external_cep_services.config.RestExternalCepServicesConfig;
import com.bellotapps.webapps_commons.exceptions.ExternalServiceException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

/**
 * A port out of the application that allows sending requests to the evaluations service.
 */
@Component
public class RestTemplateEvaluationsService implements EvaluationsService {

    private static final String EXAMS_PATH = "/exams";
    private static final String EXAM_ID_VARIABLE = "examId";

    /**
     * The {@link RestTemplate} used to communicate with the evaluations service.
     */
    private final RestTemplate restTemplate;
    /**
     * An {@link UriComponents} instance which can be configured with a variables {@link Map} to get an {@link Exam}'s
     * concrete url.
     */
    private final UriComponents examByIdUri;


    /**
     * Constructor.
     *
     * @param loadBalancedRestTemplate The {@link RestTemplate} used to communicate with the evaluations service.
     * @param properties               An {@link RestExternalCepServicesConfig.EvaluationsServiceProperties} instance used to get
     *                                 the evaluations service's base url.
     */
    public RestTemplateEvaluationsService(
            final RestTemplate loadBalancedRestTemplate,
            final RestExternalCepServicesConfig.EvaluationsServiceProperties properties) {
        Assert.notNull(properties, "The properties instance must not be null");
        Assert.hasText(properties.getBaseUrl(), "The evaluations service's base url must not be blank");
        this.restTemplate = loadBalancedRestTemplate;
        this.examByIdUri = UriComponentsBuilder.fromUriString(properties.getBaseUrl())
                .path(Constants.INTERNAL_PATH)
                .path(EXAMS_PATH)
                .path("/{" + EXAM_ID_VARIABLE + "}")
                .build()
        ;

    }


    @Override
    public Optional<Exam> getExamById(final long id) throws ExternalServiceException {
        final URI uri = examByIdUri.expand(Map.of(EXAM_ID_VARIABLE, id)).toUri();
        try {
            return Optional.ofNullable(restTemplate.getForObject(uri, ExamDto.class)).map(ExamDto::toModel);
        } catch (final HttpClientErrorException.NotFound notFound) {
            return Optional.empty();
        } catch (final RestClientException e) {
            throw new ExternalServiceException(
                    "evaluations-service", "Unexpected error when communicating with the evaluations service", e
            );
        }
    }
}
