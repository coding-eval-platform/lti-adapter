package ar.edu.itba.cep.lti_service.external_cep_services.evaluations_service;

import lombok.NonNull;
import lombok.Value;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Represents an exam as it is returned from the evaluations services.
 */
@Value
public class Exam {

    /**
     * The exam's id.
     */
    private final long id;
    /**
     * The exam's description.
     */
    @NonNull
    private final String description;
    /**
     * The {@link LocalDateTime} at which the exam should start.
     */
    @NonNull
    private final LocalDateTime startingAt;
    /**
     * The {@link Duration} that the exam should have.
     */
    @NonNull
    private final Duration duration;
    /**
     * The exam's {@link State} (i.e upcoming, in progress or finished).
     */
    @NonNull
    private final State state;
    /**
     * The maximum score the exam can have.
     */
    private final int maxScore;

    // ================================
    // Exam states
    // ================================

    /**
     * An enum containing the different states in which an exam can be.
     */
    public enum State {
        /**
         * Indicates that an exam has not started yet.
         */
        UPCOMING,
        /**
         * Indicates that the exam is being taken right now.
         */
        IN_PROGRESS,
        /**
         * Indicates that the exam has already finished.
         */
        FINISHED,
        ;
    }
}
