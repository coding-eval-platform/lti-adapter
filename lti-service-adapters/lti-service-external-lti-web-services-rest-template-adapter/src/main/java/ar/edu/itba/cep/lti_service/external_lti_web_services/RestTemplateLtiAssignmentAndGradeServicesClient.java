package ar.edu.itba.cep.lti_service.external_lti_web_services;

import ar.edu.itba.cep.lti_service.external_lti_web_services.oauth2.OAuth2Client;
import ar.edu.itba.cep.lti_service.models.ExamTaking;
import com.bellotapps.webapps_commons.exceptions.ExternalServiceException;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Concrete implementation of {@link LtiAssignmentAndGradeServicesClient}.
 */
@Component
@AllArgsConstructor
public class RestTemplateLtiAssignmentAndGradeServicesClient implements LtiAssignmentAndGradeServicesClient {

    private static final String SCORE_SCOPE = "https://purl.imsglobal.org/spec/lti-ags/scope/score";
    private static final String STARTED_ACTIVITY_PROGRESS = "Started";
    private static final String COMPLETED_ACTIVITY_PROGRESS = "Completed";
    private static final String NOT_READY_GRADING_PROGRESS = "NotReady";
    private static final String FULLY_GRADED_GRADING_PROGRESS = "FullyGraded";

    private static final String URL_TEMPLATE = "{0}/scores";

    /**
     * The {@link OAuth2Client} needed to get an authorization token that allows publishing scores.
     */
    private final OAuth2Client oAuth2Client;
    /**
     * The {@link RestTemplate} used to communicate with the LMS.
     */
    private final RestTemplate restTemplate;


    @Override
    public void publishScore(final ExamTaking examTaking, final int score) {
        final String url = MessageFormat.format(URL_TEMPLATE, examTaking.getLineItemUrl());
        final var subject = examTaking.getSubject();
        try {
            final var accessToken = getAccessToken(examTaking);
            startGrade(accessToken, subject, url);
            completeGrade(accessToken, subject, score, examTaking.getMaxScore(), url);
        } catch (final Throwable e) {
            throw new ExternalServiceException(
                    examTaking.getToolDeployment().getIssuer(),
                    "Could not communicate with the LMS",
                    e);
        }

    }

    /**
     * Retrieves an access token that allows publishing scores.
     *
     * @param examTaking The {@link ExamTaking} that contains the
     *                   {@link ar.edu.itba.cep.lti_service.models.ToolDeployment} needed by the {@link OAuth2Client}.
     * @return The created access token.
     */
    private String getAccessToken(final ExamTaking examTaking) {
        return oAuth2Client.getAccessToken(examTaking.getToolDeployment(), SCORE_SCOPE);
    }

    /**
     * Performs the start activity step of the grading process.
     *
     * @param accessToken The access token that allows publishing scores.
     * @param subject     The subject for which the score is being published.
     * @param url         The url to which the score must be published.
     */
    private void startGrade(final String accessToken, final String subject, final String url) {
        grade(
                accessToken,
                url,
                () -> ScorePublishRequest.builder()
                        .userId(subject)
                        .activityProgress(STARTED_ACTIVITY_PROGRESS)
                        .gradingProgress(NOT_READY_GRADING_PROGRESS)
        );
    }

    /**
     * @param accessToken The access token that allows publishing scores.
     * @param subject     The subject for which the score is being published.
     * @param score       The score being assigned.
     * @param maxScore    The max. score that can be achieved.
     * @param accessToken The access token that allows publishing scores.
     */
    private void completeGrade(
            final String accessToken, final String subject, final int score, final int maxScore, final String url) {
        grade(
                accessToken,
                url,
                () -> ScorePublishRequest.builder()
                        .userId(subject)
                        .scoreGiven(score)
                        .scoreMaximum(maxScore)
                        .activityProgress(COMPLETED_ACTIVITY_PROGRESS)
                        .gradingProgress(FULLY_GRADED_GRADING_PROGRESS)
        );
    }

    /**
     * Sends a grading request.
     *
     * @param accessToken     The access token that allows publishing scores.
     * @param builderSupplier A {@link Supplier} o {@link ScorePublishRequest.Builder}
     *                        that retrieves an already configured instance of the builder with information that must
     *                        be sent.
     */
    private void grade(
            final String accessToken,
            final String url,
            final Supplier<ScorePublishRequest.Builder> builderSupplier) {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.setBearerAuth(accessToken);
        final var body = builderSupplier.get().timestamp(Instant.now()).build();
        restTemplate.postForObject(url, new HttpEntity<>(body, headers), Map.class);
    }


    /**
     * Represents a score publishing request.
     */
    @Value
    @Builder(builderClassName = "Builder")
    private static final class ScorePublishRequest {

        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final String userId;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final Integer scoreGiven;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final Integer scoreMaximum;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final String activityProgress;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final String gradingProgress;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        private final String comment;
        @JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ", timezone = "UTC")
        private final Instant timestamp;
    }
}
