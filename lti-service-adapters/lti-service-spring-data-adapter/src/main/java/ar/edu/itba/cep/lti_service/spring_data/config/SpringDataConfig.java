package ar.edu.itba.cep.lti_service.spring_data.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuration class for Spring Data Jpa Repositories.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.lti_service.spring_data"
})
@EnableJpaRepositories(basePackages = {
        "ar.edu.itba.cep.lti_service.spring_data.interfaces"
})
public class SpringDataConfig {
}
