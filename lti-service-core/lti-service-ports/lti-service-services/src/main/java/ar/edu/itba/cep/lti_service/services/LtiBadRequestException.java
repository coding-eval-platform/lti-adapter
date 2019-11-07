package ar.edu.itba.cep.lti_service.services;

import com.bellotapps.webapps_commons.exceptions.UnauthenticatedException;

/**
 * An extension of {@link UnauthenticatedException} specifically to be thrown when there is any issue with the
 * authentication process of the LTI specification.
 */
public class LtiBadRequestException extends RuntimeException {

    /**
     * Default constructor.
     */
    public LtiBadRequestException() {
        super();
    }

    /**
     * Constructor which can set a {@code message}.
     *
     * @param message The detail message, which is saved for later retrieval by the {@link #getMessage()} method.
     */
    public LtiBadRequestException(String message) {
        super(message);
    }

    /**
     * Constructor which can set a mes{@code message} and a {@code cause}.
     *
     * @param message The detail message, which is saved for later retrieval by the {@link #getMessage()} method.
     * @param cause   The cause (which is saved for later retrieval by the {@link #getCause()} method).
     *                For more information, see {@link RuntimeException#RuntimeException(Throwable)}.
     */
    public LtiBadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
