package ar.edu.itba.cep.lti_service.rest.config;

import com.bellotapps.webapps_commons.config.JerseyConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * An object in charge of customizing the built-in {@link JerseyConfig}.
 */
@Component
public class JerseyConfigCustomizer implements InitializingBean {

    /**
     * The {@link JerseyConfig} to be modified.
     */
    private final JerseyConfig jerseyConfig;

    /**
     * Constructor.
     *
     * @param jerseyConfig The {@link JerseyConfig} to be modified.
     */
    @Autowired
    public JerseyConfigCustomizer(final JerseyConfig jerseyConfig) {
        this.jerseyConfig = jerseyConfig;
    }

    @Override
    public void afterPropertiesSet() {
        jerseyConfig.property(ServerProperties.WADL_FEATURE_DISABLE, true);
    }
}
