package ar.edu.itba.cep.lti_service.domain.config;

import ar.edu.itba.cep.security.EnableKeyFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the domain's module.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.lti_service.domain"
})
@EnableKeyFactory
public class DomainConfig {
}
