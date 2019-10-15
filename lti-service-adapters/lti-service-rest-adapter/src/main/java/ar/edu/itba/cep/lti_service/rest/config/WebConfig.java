package ar.edu.itba.cep.lti_service.rest.config;

import com.bellotapps.webapps_commons.config.EnableJerseyApplication;
import com.bellotapps.webapps_commons.exceptions.CustomConstraintViolationException;
import com.bellotapps.webapps_commons.validation.jersey.ConstraintViolationExceptionCreator;
import com.bellotapps.webapps_commons.validation.jersey.EnableJerseyValidation;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.filter.ForwardedHeaderFilter;

/**
 * Configuration class in charge of configuring web concerns.
 */
@Configuration
@ComponentScan(basePackages = {
        "ar.edu.itba.cep.lti_service.rest.controller"
})
@EnableJerseyApplication(basePackages = {
        "ar.edu.itba.cep.lti_service.rest.controller.endpoints",
        "com.bellotapps.webapps_commons.data_transfer.jersey.providers",
}, errorHandlersPackages = {
        "com.bellotapps.webapps_commons.error_handlers",
})
@EnableJerseyValidation
public class WebConfig {

    /**
     * A bean of a {@link CustomConstraintViolationException}.
     *
     * @return A {@link CustomConstraintViolationException} bean.
     */
    @Bean
    public ConstraintViolationExceptionCreator constraintViolationExceptionCreator() {
        return CustomConstraintViolationException::new;
    }

    /**
     * Creates a {@link FilterRegistrationBean} for a {@link ForwardedHeaderFilter}, in order to process the
     * Forwarded and X-Forwarded-* headers.
     *
     * @return The {@link FilterRegistrationBean}.
     */
    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
        final var bean = new FilterRegistrationBean<ForwardedHeaderFilter>();
        bean.setFilter(new ForwardedHeaderFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE + 10);
        return bean;
    }
}
