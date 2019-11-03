package ar.edu.itba.cep.lti_service.rest.controller.validation;

import ar.edu.itba.cep.lti_service.rest.controller.dtos.ToolDeploymentDto;
import ar.edu.itba.cep.security.KeyHelper;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * A {@link ConstraintValidator} that will verify that a {@link ToolDeploymentDto} carries
 * a valid private key and {@link io.jsonwebtoken.SignatureAlgorithm}.
 */
public class ValidPrivateKeyAndSignatureAlgorithmValidator
        implements ConstraintValidator<ValidPrivateKeyAndSignatureAlgorithm, ToolDeploymentDto> {

    @Override
    public boolean isValid(final ToolDeploymentDto toolDeploymentDto, final ConstraintValidatorContext context) {
        if (toolDeploymentDto == null) {
            return true; // We are not performing null validation.
        }

        // Perform the exact assertion the ToolDeployment model applies over its arguments
        final var privateKey = toolDeploymentDto.getPrivateKey();
        final var algorithm = toolDeploymentDto.getSignatureAlgorithm();

        try {
            final var keyFactory = KeyFactory.getInstance(algorithm.getFamilyName());
            KeyHelper.generateKey(
                    keyFactory, privateKey, PKCS8EncodedKeySpec::new, KeyFactory::generatePrivate
            );
            return true;
        } catch (final NoSuchAlgorithmException e) {
            return true; // We are not validating this.
        } catch (final KeyHelper.InvalidKeyException e) {
            return false;
        }
    }
}
