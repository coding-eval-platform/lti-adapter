package ar.edu.itba.cep.lti_service.external_cep_services.evaluations_service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Data transfer object used to receive {@link Exam}s data.
 */
@Value
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
/* package */ class ExamDto {

    /**
     * The exam's id.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final long id;
    /**
     * The exam's description.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final String description;
    /**
     * The {@link LocalDateTime} at which the exam should start.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final LocalDateTime startingAt;
    /**
     * The {@link Duration} that the exam should have.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonDeserialize(using = Java8DurationToMinutesDeserializer.class)
    private final Duration duration;
    /**
     * The exam's {@link Exam.State} (i.e upcoming, in progress or finished).
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final Exam.State state;
    /**
     * The maximum score the exam can have.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private final int maxScore;

    /**
     * Maps {@code this} instance into an {@link Exam}.
     *
     * @return The created {@link Exam}.
     */
    /* package */ Exam toModel() {
        return new Exam(id, description, startingAt, duration, state, maxScore);
    }


    /**
     * {@link com.fasterxml.jackson.databind.JsonDeserializer} to transform a {@link String} into a {@link Duration},
     * reading an integer number that represents minutes.
     */
    /* package */ static final class Java8DurationToMinutesDeserializer extends StdDeserializer<Duration> {

        /**
         * Default constructor.
         */
        protected Java8DurationToMinutesDeserializer() {
            super(Duration.class);
        }

        @Override
        public Duration deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
            final var durationAsText = p.getText();
            final var durationAsLong = Long.parseLong(durationAsText);
            return Duration.ofMinutes(durationAsLong);
        }
    }
}
