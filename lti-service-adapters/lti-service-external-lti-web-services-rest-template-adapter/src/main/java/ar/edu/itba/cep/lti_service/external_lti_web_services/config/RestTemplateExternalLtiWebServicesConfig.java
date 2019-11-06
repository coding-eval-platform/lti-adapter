package ar.edu.itba.cep.lti_service.external_lti_web_services.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for the {@link RestTemplate} External LTI Web Services.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.lti_service.external_lti_web_services"
})
@EnableConfigurationProperties({
        RestTemplateExternalLtiWebServicesConfig.LtiWebServicesProperties.class,
})
public class RestTemplateExternalLtiWebServicesConfig {

    /**
     * A load balanced {@link RestTemplate}.
     *
     * @param restTemplateBuilder The {@link RestTemplateBuilder} used to create the {@link RestTemplate} instance.
     * @return The created {@link RestTemplate}.
     */
    @Bean
    public RestTemplate restTemplate(final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }

    @Data
    @ConfigurationProperties("external-lti-web-services")
    public static final class LtiWebServicesProperties {
        /**
         * The duration of the assertion JWT.
         */
        private int assertionJwtDuration = 5;
    }
}
