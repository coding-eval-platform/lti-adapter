package ar.edu.itba.cep.lti_service.rest.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class in charge of configuring web concerns.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.lti_service.rest.controller"
})
public class WebConfig {
}
