package ar.edu.itba.cep.lti_service.rest.controller.validation;

import ar.edu.itba.cep.lti_service.rest.controller.dtos.ToolDeploymentDto;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;

/**
 * A {@link ConstraintValidator} that will verify that a {@link ToolDeploymentDto} carries
 * a valid private key and {@link io.jsonwebtoken.SignatureAlgorithm}.
 */
public class ValidSignatureAlgorithmValidator
        implements ConstraintValidator<ValidSignatureAlgorithm, SignatureAlgorithm> {

    @Override
    public boolean isValid(final SignatureAlgorithm signatureAlgorithm, final ConstraintValidatorContext context) {
        if (signatureAlgorithm == null) {
            return true; // We don't validate if its null in this validator.
        }

        try {
            KeyFactory.getInstance(signatureAlgorithm.getFamilyName());
            return true;
        } catch (final NoSuchAlgorithmException e) {
            return false;
        }
    }
}
