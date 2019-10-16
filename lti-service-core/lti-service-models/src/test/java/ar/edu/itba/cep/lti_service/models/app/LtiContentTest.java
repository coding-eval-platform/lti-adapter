package ar.edu.itba.cep.lti_service.models.app;

import ar.edu.itba.cep.lti_service.models.admin.ToolDeployment;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;

/**
 * Test class for {@link LtiContent}.
 */
class LtiContentTest {


    // ================================================================================================================
    // Acceptable arguments
    // ================================================================================================================

    /**
     * Tests the creation of a {@link ToolDeployment} instance.
     */
    @Test
    void testCreation() {
        final var url = url();
        final var response = new LtiContent(url);
        Assertions.assertAll(
                "Creating an LtiContent is not working as expected",
                () -> Assertions.assertEquals(new URI(url), response.getUri(), "The URI does not match")
        );
    }


    // ================================================================================================================
    // Constraint testing
    // ================================================================================================================

    /**
     * Tests that a null deployment id is not allowed when creating a {@link ToolDeployment}
     * (i.e throws an {@link IllegalArgumentException}).
     */
    @Test
    void testNullUrl() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new LtiContent(null),
                "Creating an LtiContent with a null url is being allowed"
        );
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * @return A valid LTI message hint.
     */
    private static String url() {
        return "https://"
                + Faker.instance().internet().domainName()
                + "/some-path?state="
                + Faker.instance().lorem().word()
                + "#"
                + Faker.instance().lorem().word()
                ;
    }
}
