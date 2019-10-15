package ar.edu.itba.cep.lti_service.models.admin;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * Represents a front end deployment
 * (i.e contains endpoints to which the consumer of this service is redirected when performing LTI requests).
 */
@Getter
@NoArgsConstructor(force = true)
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(of = "id", doNotUseGetters = true)
public class FrontendDeployment {

    /**
     * The variable definition for exams' ids in an "exam creation" url template.
     */
    public static final String EXAM_ID_VARIABLE = "${exam-id}";

    /**
     * The variable definition for JWTs in an "exam creation" url template.
     */
    public static final String JWT_VARIABLE = "${jwt}";


    /**
     * The frontend deployment id (this is an internal id).
     */
    private final UUID id;
    /**
     * The url at which the "exam creation" form is deployed.
     */
    private final String examCreationUrl;
    /**
     * The url template at which the "exam taking" feature is deployed.
     */
    private final String examTakingUrlTemplate;


    /**
     * Constructor.
     *
     * @param examCreationUrl       The url at which the "exam creation" form is deployed.
     * @param examTakingUrlTemplate The url template at which the "exam taking" feature is deployed.
     * @throws IllegalArgumentException In case any value is not a valid one.
     */
    public FrontendDeployment(final String examCreationUrl, final String examTakingUrlTemplate)
            throws IllegalArgumentException {
        assertExamCreationUrl(examCreationUrl);
        assertExamTakingUrlTemplate(examTakingUrlTemplate);

        this.id = null;
        this.examCreationUrl = examCreationUrl;
        this.examTakingUrlTemplate = examTakingUrlTemplate;
    }


    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code examCreationUrl} is valid.
     *
     * @param examCreationUrl The "exam creation" url to be checked.
     * @throws IllegalArgumentException In case the "exam creation" url is not valid.
     */
    private static void assertExamCreationUrl(final String examCreationUrl) throws IllegalArgumentException {
        Assert.notNull(examCreationUrl, "The \"exam creation\" url must not be null");
    }

    /**
     * Asserts that the given {@code examTakingUrlTemplate} is valid.
     *
     * @param examTakingUrlTemplate The "exam taking" url template to be checked.
     * @throws IllegalArgumentException In case the "exam taking" url template is not valid.
     */
    private static void assertExamTakingUrlTemplate(final String examTakingUrlTemplate) throws IllegalArgumentException {
        Assert.notNull(examTakingUrlTemplate, "The \"exam taking\" url template must not be null");
        Assert.isTrue(
                examTakingUrlTemplate.contains(EXAM_ID_VARIABLE),
                "The \"exam taking\" url template must contain the exam id variable (" + EXAM_ID_VARIABLE + ")"
        );
        Assert.isTrue(
                examTakingUrlTemplate.contains(JWT_VARIABLE),
                "The \"exam taking\" url template must contain the jwt variable (" + JWT_VARIABLE + ")"
        );
    }
}
