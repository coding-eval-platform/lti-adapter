package ar.edu.itba.cep.lti_service.rest.controller.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated {@link io.jsonwebtoken.SignatureAlgorithm} must be allowed by the system
 * (i.e a {@link java.security.KeyFactory} instance with the {@link io.jsonwebtoken.SignatureAlgorithm}'s family
 * must be allowed to be created).
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)

@Documented
@Constraint(validatedBy = {
        ValidSignatureAlgorithmValidator.class
})
@Repeatable(ValidSignatureAlgorithm.List.class)
public @interface ValidSignatureAlgorithm {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@code @ValidSignatureAlgorithm} constraints on the same element.
     *
     * @see ValidSignatureAlgorithm
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        ValidSignatureAlgorithm[] value();
    }
}
