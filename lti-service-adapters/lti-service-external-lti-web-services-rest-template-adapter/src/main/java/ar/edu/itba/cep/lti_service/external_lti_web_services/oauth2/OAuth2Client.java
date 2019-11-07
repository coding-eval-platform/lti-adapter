package ar.edu.itba.cep.lti_service.external_lti_web_services.oauth2;

import ar.edu.itba.cep.lti_service.models.ToolDeployment;
import com.bellotapps.webapps_commons.exceptions.ExternalServiceException;

import java.util.Arrays;
import java.util.List;

/**
 * Defines behaviour for objects that can obtain access tokens.
 */
public interface OAuth2Client {

    /**
     * Retrieves an access token according to the given {@code toolDeployment}, with the given {@code scopes}.
     *
     * @param toolDeployment The {@link ToolDeployment} with data needed to get the token
     *                       (e.g application key, application secret, authentication endpoint, etc.).
     * @param scopes         The scopes that must be enabled by the token.
     * @return The retrieved access token.
     * @throws ExternalServiceException If there is any issue when communicating with the authorization server.
     */
    String getAccessToken(final ToolDeployment toolDeployment, final List<String> scopes)
            throws ExternalServiceException;

    /**
     * Retrieves an access token according to the given {@code toolDeployment}, with the given {@code scopes}.
     *
     * @param toolDeployment The {@link ToolDeployment} with data needed to get the token
     *                       (e.g application key, application secret, authentication endpoint, etc.).
     * @param scopes         The scopes that must be enabled by the token.
     * @return The retrieved access token.
     * @throws ExternalServiceException If there is any issue when communicating with the authorization server.
     */
    default String getAccessToken(final ToolDeployment toolDeployment, final String... scopes)
            throws ExternalServiceException {
        return getAccessToken(toolDeployment, Arrays.asList(scopes));
    }
}
