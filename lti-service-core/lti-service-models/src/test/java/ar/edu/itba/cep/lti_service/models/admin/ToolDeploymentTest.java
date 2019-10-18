package ar.edu.itba.cep.lti_service.models.admin;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * Test class for {@link ToolDeployment}.
 */
class ToolDeploymentTest {


    // ================================================================================================================
    // Acceptable arguments
    // ================================================================================================================

    /**
     * Tests the creation of a {@link ToolDeployment} instance.
     */
    @Test
    void testCreation() {
        final var deploymentId = deploymentId();
        final var clientId = clientId();
        final var issuer = issuer();
        final var oidcAuthenticationEndpoint = oidcAuthenticationEndpoint();
        final var jwksEndpoint = jwksEndpoint();

        final var toolDeployment = new ToolDeployment(
                deploymentId,
                clientId,
                issuer,
                oidcAuthenticationEndpoint,
                jwksEndpoint
        );

        Assertions.assertAll(
                "Creating a ToolDeployment is not working as expected",
                () -> Assertions.assertEquals(deploymentId, toolDeployment.getDeploymentId(), "Deployment id does not match"),
                () -> Assertions.assertEquals(clientId, toolDeployment.getClientId(), "Client id does not match"),
                () -> Assertions.assertEquals(issuer, toolDeployment.getIssuer(), "Issuer does not match"),
                () -> Assertions.assertEquals(
                        oidcAuthenticationEndpoint,
                        toolDeployment.getOidcAuthenticationEndpoint(),
                        "The Open-Id Connect authentication endpoint does not match"
                ),
                () -> Assertions.assertEquals(
                        jwksEndpoint,
                        toolDeployment.getJwksEndpoint(),
                        "The JWKS endpoint does not match"
                )
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
    void testNullDeploymentId() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ToolDeployment(null, clientId(), issuer(), oidcAuthenticationEndpoint(), jwksEndpoint()),
                "Creating a ToolDeployment with a null deployment id is being allowed"
        );
    }

    /**
     * Tests that a null client id is not allowed when creating a {@link ToolDeployment}
     * (i.e throws an {@link IllegalArgumentException}).
     */
    @Test
    void testNullClientId() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ToolDeployment(deploymentId(), null, issuer(), oidcAuthenticationEndpoint(), jwksEndpoint()),
                "Creating a ToolDeployment with a null client id is being allowed"
        );
    }

    /**
     * Tests that a null issuer is not allowed when creating a {@link ToolDeployment}
     * (i.e throws an {@link IllegalArgumentException}).
     */
    @Test
    void testNullIssuer() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ToolDeployment(deploymentId(), clientId(), null, oidcAuthenticationEndpoint(), jwksEndpoint()),
                "Creating a ToolDeployment with a null issuer is being allowed"
        );
    }

    /**
     * Tests that a null Open-Id Connect authentication endpoint is not allowed when creating a {@link ToolDeployment}
     * (i.e throws an {@link IllegalArgumentException}).
     */
    @Test
    void testNullOidcAuthenticationEndpoint() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ToolDeployment(deploymentId(), clientId(), issuer(), null, jwksEndpoint()),
                "Creating a ToolDeployment with a null Open-Id Connection authentication endpoint id is being allowed"
        );
    }

    /**
     * Tests that a null JWKS endpoint is not allowed when creating a {@link ToolDeployment}
     * (i.e throws an {@link IllegalArgumentException}).
     */
    @Test
    void testNullJwksEndpoint() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ToolDeployment(deploymentId(), clientId(), issuer(), oidcAuthenticationEndpoint(), null),
                "Creating a ToolDeployment with a null JWKS endpoint is being allowed"
        );
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * @return A valid deployment id.
     */
    private static String deploymentId() {
        return UUID.randomUUID().toString();
    }

    /**
     * @return A valid client id.
     */
    private static String clientId() {
        return UUID.randomUUID().toString();
    }

    /**
     * @return A valid issuer.
     */
    private static String issuer() {
        return "https://" + Faker.instance().internet().domainName();
    }

    /**
     * @return A valid Open-Id Connect authentication endpoint
     */
    private static String oidcAuthenticationEndpoint() {
        return "https://" + Faker.instance().internet().domainName() + "/oidc";
    }

    /**
     * @return A valid JWKS endpoint
     */
    private static String jwksEndpoint() {
        return "https://" + Faker.instance().internet().domainName() + "/jwks";
    }
}
