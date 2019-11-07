package ar.edu.itba.cep.lti_service.security;


import ar.edu.itba.cep.security.EnableJwtAuthentication;
import ar.edu.itba.cep.security.EnableRestSecurity;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for security aspects at the web layer.
 */
@Configuration
@EnableRestSecurity
@EnableJwtAuthentication
public class WebSecurityConfig {
}
