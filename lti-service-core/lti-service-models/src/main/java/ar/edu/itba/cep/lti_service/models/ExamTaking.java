package ar.edu.itba.cep.lti_service.models;

import lombok.*;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * Represents an exam being taken by a subject (i.e a user of an LMS).
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(of = "id", doNotUseGetters = true)
public class ExamTaking {

    /**
     * The exam taking id.
     */
    private final UUID id;
    /**
     * The id of the exam being taken.
     */
    private final long examId;
    /**
     * The subject that is taking the exam (i.e the user id).
     */
    private final String subject;
    /**
     * The line-item url, used to publish the score in the LMS.
     */
    private final String lineItemUrl;
    /**
     * The max. amount of score that can be obtained in the exam.
     */
    private final int maxScore;
    /**
     * The {@link ToolDeployment} representing the integration with the LMS.
     */
    private final ToolDeployment toolDeployment;


    // ================================================================================================================
    // Assertions
    // ================================================================================================================

    /**
     * Verifies that the given {@code subject} is valid.
     *
     * @param subject The subject to be validated.
     * @throws IllegalArgumentException If the subject is not valid.
     */
    private static void assertSubject(final String subject) throws IllegalArgumentException {
        Assert.hasText(subject, "The subject must not be null, empty or blank");
    }

    /**
     * Verifies that the given {@code lineItemUrl} is valid.
     *
     * @param lineItemUrl The line-item url to be validated.
     * @throws IllegalArgumentException If the line-item url is not valid.
     */
    private static void assertLineItemUrl(final String lineItemUrl) throws IllegalArgumentException {
        Assert.hasText(lineItemUrl, "The line-item url must not be null, empty or blank");
    }

    /**
     * Verifies that the given {@code maxScore} is valid.
     *
     * @param maxScore The max. score to be validated.
     * @throws IllegalArgumentException If the max. score is not valid.
     */
    private static void assertMaxScore(final int maxScore) throws IllegalArgumentException {
        Assert.isTrue(maxScore > 0, "The max. score must be positive");
    }

    /**
     * Verifies that the given {@code toolDeployment} is valid.
     *
     * @param toolDeployment The {@link ToolDeployment} to be validated.
     * @throws IllegalArgumentException If the {@link ToolDeployment} is not valid.
     */
    private static void assertToolDeployment(final ToolDeployment toolDeployment) throws IllegalArgumentException {
        Assert.notNull(toolDeployment, "The tool deployment must not be null");
    }


    // ================================================================================================================
    // Creators
    // ================================================================================================================

    /**
     * Creates an {@link ExamTaking} with a {@code null} id.
     *
     * @param examId         The id of the exam being taken.
     * @param subject        The subject that is taking the exam (i.e the user id).
     * @param lineItemUrl    The line-item url, used to publish the score in the LMS.
     * @param maxScore       The max. amount of score that can be obtained in the exam.
     * @param toolDeployment The {@link ToolDeployment} representing the integration with the LMS.
     * @return The created {@link ExamTaking}.
     * @throws IllegalArgumentException If any argument is not valid.
     */
    public static ExamTaking withoutId(
            final long examId,
            final String subject,
            final String lineItemUrl,
            final int maxScore,
            final ToolDeployment toolDeployment) throws IllegalArgumentException {
        assertSubject(subject);
        assertLineItemUrl(lineItemUrl);
        assertToolDeployment(toolDeployment);
        assertMaxScore(maxScore);

        return new ExamTaking(null, examId, subject, lineItemUrl, maxScore, toolDeployment);
    }
}
