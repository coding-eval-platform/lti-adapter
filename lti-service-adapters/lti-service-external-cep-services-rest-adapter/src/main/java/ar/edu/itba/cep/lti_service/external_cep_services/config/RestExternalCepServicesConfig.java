package ar.edu.itba.cep.lti_service.external_cep_services.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configuration class for the REST External CEP Services.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.lti_service.external_cep_services"
})
@EnableConfigurationProperties({
        RestExternalCepServicesConfig.EvaluationsServiceProperties.class,
        RestExternalCepServicesConfig.TokensServiceProperties.class,
})
public class RestExternalCepServicesConfig {

    /**
     * A load balanced {@link RestTemplate}.
     *
     * @param restTemplateBuilder The {@link RestTemplateBuilder} used to create the {@link RestTemplate} instance.
     * @return The created {@link RestTemplate}.
     */
    @Bean
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate(final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.build();
    }


    @Data
    @ConfigurationProperties("evaluations-service")
    public static final class EvaluationsServiceProperties {
        /**
         * The base url where the Evaluations service is serving.
         */
        private String baseUrl = "http://evaluations-service/";
    }

    @Data
    @ConfigurationProperties("tokens-service")
    public static final class TokensServiceProperties {
        /**
         * The base url where the Tokens service is serving.
         */
        private String baseUrl = "http://users-service/";
    }
}
