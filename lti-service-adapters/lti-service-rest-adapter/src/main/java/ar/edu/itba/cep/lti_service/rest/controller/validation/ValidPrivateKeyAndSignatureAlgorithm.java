package ar.edu.itba.cep.lti_service.rest.controller.validation;

import ar.edu.itba.cep.lti_service.rest.controller.dtos.ToolDeploymentDto;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated {@link ToolDeploymentDto}
 * must contain a private key that is compatible with its {@link io.jsonwebtoken.SignatureAlgorithm}.
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)

@Documented
@Constraint(validatedBy = {
        ValidPrivateKeyAndSignatureAlgorithmValidator.class
})
@Repeatable(ValidPrivateKeyAndSignatureAlgorithm.List.class)
public @interface ValidPrivateKeyAndSignatureAlgorithm {

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Defines several {@code @PrivateKeyAndSignatureAlgorithm} constraints on the same element.
     *
     * @see ValidPrivateKeyAndSignatureAlgorithm
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        ValidPrivateKeyAndSignatureAlgorithm[] value();
    }
}
