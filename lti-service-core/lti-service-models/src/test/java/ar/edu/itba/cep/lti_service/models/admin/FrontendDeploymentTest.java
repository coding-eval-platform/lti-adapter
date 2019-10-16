package ar.edu.itba.cep.lti_service.models.admin;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static ar.edu.itba.cep.lti_service.models.admin.FrontendDeployment.EXAM_ID_VARIABLE;
import static ar.edu.itba.cep.lti_service.models.admin.FrontendDeployment.JWT_VARIABLE;


/**
 * Test class for {@link FrontendDeployment}.
 */
class FrontendDeploymentTest {

    // ================================================================================================================
    // Acceptable arguments
    // ================================================================================================================

    /**
     * Tests the creation of a {@link FrontendDeployment} instance.
     */
    @Test
    void testCreation() {
        final var examCreationUrl = examCreationUrl();
        final var examTakingUrlTemplate = validExamTakingUrlTemplate();

        final var frontendDeployment = new FrontendDeployment(
                examCreationUrl,
                examTakingUrlTemplate
        );

        Assertions.assertAll(
                "Creating a FrontendDeployment is not working as expected",
                () -> Assertions.assertEquals(
                        examCreationUrl,
                        frontendDeployment.getExamCreationUrl(),
                        "The Exam Creation url does not match"
                ),
                () -> Assertions.assertEquals(
                        examTakingUrlTemplate,
                        frontendDeployment.getExamTakingUrlTemplate(),
                        "The Exam Taking url template does not match"
                )
        );
    }


    // ================================================================================================================
    // Constraint testing
    // ================================================================================================================

    /**
     * Tests that a null "exam creation" url is not allowed when creating a {@link FrontendDeployment}
     * (i.e throws an {@link IllegalArgumentException}).
     */
    @Test
    void testNullExamCreationUrl() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new FrontendDeployment(null, validExamTakingUrlTemplate()),
                "Creating a FrontendDeployment with a null \"Exam Creation\" url is being allowed"
        );
    }

    /**
     * Tests that a null "exam taking" url template is not allowed when creating a {@link FrontendDeployment}
     * (i.e throws an {@link IllegalArgumentException}).
     */
    @Test
    void testNullExamTakingUrlTemplate() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new FrontendDeployment(examCreationUrl(), null),
                "Creating a FrontendDeployment with a null \"Exam Taking\" url template is being allowed"
        );
    }

    /**
     * Tests that an "exam taking" url template without the needed variables is not allowed
     * when creating a {@link FrontendDeployment} (i.e throws an {@link IllegalArgumentException}).
     */
    @Test
    void testMissingVariablesExamTakingUrlTemplate() {
        Assertions.assertAll(
                "Creating a FrontendDeployment with an \"Exam Taking\" url template" +
                        " with missing variables is being allowed",
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new FrontendDeployment(examCreationUrl(), missingExamIdVariableExamTakingUrlTemplate()),
                        "It is allowed without the exam id variable"
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new FrontendDeployment(examCreationUrl(), missingJwtVariableExamTakingUrlTemplate()),
                        "It is allowed without the jwt variable"
                ),
                () -> Assertions.assertThrows(
                        IllegalArgumentException.class,
                        () -> new FrontendDeployment(examCreationUrl(), basicExamTakingUrl()),
                        "It is allowed without both the exam id and jwt variables"
                )
        );
    }


    // ================================================================================================================
    // Helpers
    // ================================================================================================================

    /**
     * @return A valid "exam creation" url.
     */
    private static String examCreationUrl() {
        return "https://" + Faker.instance().internet().domainName() + "/create-exam";
    }

    /**
     * @return A valid "exam taking" url template.
     */
    private static String validExamTakingUrlTemplate() {
        return basicExamTakingUrl() + "/" + EXAM_ID_VARIABLE + "?token=" + JWT_VARIABLE;
    }

    /**
     * @return An invalid "exam taking" url template (does not contain the exam id variable).
     */
    private static String missingExamIdVariableExamTakingUrlTemplate() {
        return basicExamTakingUrl() + "?token=" + JWT_VARIABLE;
    }

    /**
     * @return An invalid "exam taking" url template (does not contain the jwt variable).
     */
    private static String missingJwtVariableExamTakingUrlTemplate() {
        return basicExamTakingUrl() + "/" + EXAM_ID_VARIABLE;
    }

    /**
     * @return A basic "exam taking" url template (only schema, host, and a basic path).
     */
    private static String basicExamTakingUrl() {
        return "https://" + Faker.instance().internet().domainName() + "/take-exam";
    }
}
