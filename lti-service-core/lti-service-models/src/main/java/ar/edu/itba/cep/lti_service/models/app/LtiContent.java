package ar.edu.itba.cep.lti_service.models.app;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.util.Assert;

import java.net.URI;

/**
 * Represents an LTI content (i.e an url to the said content).
 * This is the final step in the LTI authentication flow
 * Check <a href=https://www.imsglobal.org/spec/security/v1p0/#openid_connect_launch_flow>IMS Security Framework,
 * section 5.1.1: OpenID Connect Launch Flow Overview</a> for more information.
 * This entity represents the following step:
 * <a href=https://www.imsglobal.org/spec/security/v1p0/#step-4-resource-is-displayed>
 * IMS Security Framework, section 5.1.1.4: Step 4: Resource is displayed</a>
 *
 * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#openid_connect_launch_flow>IMS Security Framework,
 * section 5.1.1: OpenID Connect Launch Flow Overview</a>.
 * @see <a href=https://www.imsglobal.org/spec/security/v1p0/#step-4-resource-is-displayed>
 * IMS Security Framework, section 5.1.1.4: Step 4: Resource is displayed</a>
 */
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
public class LtiContent {

    /**
     * The url at which the content can be accessed.
     */
    private final String url;


    /**
     * Constructor.
     *
     * @param url The url at which the content can be accessed.
     * @throws IllegalArgumentException In case any value is not a valid one.
     */
    public LtiContent(final String url) throws IllegalArgumentException {
        assertUrl(url);
        this.url = url;
    }

    // ================================
    // Getter
    // ================================

    /**
     * Creates the {@link URI} to which the user must be redirected to access the LTI resource.
     *
     * @return The created {@link URI}.
     */
    public URI getUri() {
        return URI.create(url);
    }

    /**
     * Asserts that the given {@code url} is valid.
     *
     * @param url The url to be checked.
     * @throws IllegalArgumentException In case the url is not valid.
     */
    private static void assertUrl(final String url) throws IllegalArgumentException {
        Assert.notNull(url, "The url must not be null");
    }
}
