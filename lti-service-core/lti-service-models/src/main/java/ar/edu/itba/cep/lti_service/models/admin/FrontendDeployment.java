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
     * The variable definition for states in an "exam creation" url template.
     */
    public static final String STATE_VARIABLE = "${exam-creation-state}";

    /**
     * The variable definition for exams' ids in an "exam taking" url template.
     */
    public static final String EXAM_ID_VARIABLE = "${exam-id}";
    /**
     * The variable definition for access tokens in an "exam taking" url template.
     */
    public static final String ACCESS_TOKEN_VARIABLE = "${access-token}";
    /**
     * The variable definition for refresh tokens in an "exam taking" url template.
     */
    public static final String REFRESH_TOKEN_VARIABLE = "${refresh-token}";
    /**
     * The variable definition for token ids in an "exam taking" url template.
     */
    public static final String TOKEN_ID_VARIABLE = "${token-id}";


    /**
     * The frontend deployment id (this is an internal id).
     */
    private final UUID id;
    /**
     * The url at which the "exam creation" form is deployed.
     */
    private final String examCreationUrlTemplate;
    /**
     * The url template at which the "exam taking" feature is deployed.
     */
    private final String examTakingUrlTemplate;


    /**
     * Constructor.
     *
     * @param examCreationUrlTemplate The url template which the "exam creation" form is deployed.
     * @param examTakingUrlTemplate   The url template at which the "exam taking" feature is deployed.
     * @throws IllegalArgumentException In case any value is not a valid one.
     */
    public FrontendDeployment(final String examCreationUrlTemplate, final String examTakingUrlTemplate)
            throws IllegalArgumentException {
        assertExamCreationUrlTemplate(examCreationUrlTemplate);
        assertExamTakingUrlTemplate(examTakingUrlTemplate);

        this.id = null;
        this.examCreationUrlTemplate = examCreationUrlTemplate;
        this.examTakingUrlTemplate = examTakingUrlTemplate;
    }


    // ================================
    // Assertions
    // ================================

    /**
     * Asserts that the given {@code examCreationUrlTemplate} is valid.
     *
     * @param examCreationUrlTemplate The "exam creation" url template to be checked.
     * @throws IllegalArgumentException In case the "exam creation" url template is not valid.
     */
    private static void assertExamCreationUrlTemplate(final String examCreationUrlTemplate) throws IllegalArgumentException {
        Assert.notNull(examCreationUrlTemplate, "The \"exam creation\" url template must not be null");
        Assert.isTrue(
                examCreationUrlTemplate.contains(STATE_VARIABLE),
                "The \"exam creation\" url template must contain the state variable (" + STATE_VARIABLE + ")"
        );
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
                examTakingUrlTemplate.contains(ACCESS_TOKEN_VARIABLE),
                "The \"exam taking\" url template must contain the access token variable (" + ACCESS_TOKEN_VARIABLE + ")"
        );
        Assert.isTrue(
                examTakingUrlTemplate.contains(REFRESH_TOKEN_VARIABLE),
                "The \"exam taking\" url template must contain the refresh token variable (" + REFRESH_TOKEN_VARIABLE + ")"
        );
        Assert.isTrue(
                examTakingUrlTemplate.contains(TOKEN_ID_VARIABLE),
                "The \"exam taking\" url template must contain the token id variable (" + TOKEN_ID_VARIABLE + ")"
        );
    }
}
