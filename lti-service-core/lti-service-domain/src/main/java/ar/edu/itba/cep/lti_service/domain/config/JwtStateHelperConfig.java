package ar.edu.itba.cep.lti_service.domain.config;

import ar.edu.itba.cep.lti_service.domain.helpers.AbstractJwtStateHelper;
import ar.edu.itba.cep.lti_service.domain.helpers.ExamSelectionStateHelper;
import ar.edu.itba.cep.lti_service.domain.helpers.LtiStateHelper;
import ar.edu.itba.cep.security.KeyHelper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Configuration class used to create {@link AbstractJwtStateHelper} subclasses beans.
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(JwtStateHelperConfig.JwtStateHelperProperties.class)
public class JwtStateHelperConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtStateHelperConfig.class);

    /**
     * Builds an {@link LtiStateHelper} bean.
     *
     * @param publicKey  The {@link PublicKey} needed by the {@link ExamSelectionStateHelper}.
     * @param privateKey The {@link PrivateKey} needed by the {@link ExamSelectionStateHelper}.
     * @return The created bean.
     */
    @Bean
    public LtiStateHelper ltiStateHelper(
            @Qualifier(value = "state-helper-public-key") final PublicKey publicKey,
            @Qualifier(value = "state-helper-private-key") final PrivateKey privateKey) {
        return new LtiStateHelper(publicKey, privateKey);
    }

    /**
     * Builds an {@link ExamSelectionStateHelper} bean.
     *
     * @param publicKey  The {@link PublicKey} needed by the {@link ExamSelectionStateHelper}.
     * @param privateKey The {@link PrivateKey} needed by the {@link ExamSelectionStateHelper}.
     * @return The created bean.
     */
    @Bean
    public ExamSelectionStateHelper examCreationStateHelper(
            @Qualifier(value = "state-helper-public-key") final PublicKey publicKey,
            @Qualifier(value = "state-helper-private-key") final PrivateKey privateKey) {
        return new ExamSelectionStateHelper(publicKey, privateKey);
    }


    /**
     * Builds a {@link PublicKey} from the given {@code encoded} {@link String} representation of the key.
     *
     * @param properties The {@link JwtStateHelperProperties} from where the encoded public key is taken.
     * @return The created {@link PublicKey}.
     */
    @Bean
    @Qualifier(value = "state-helper-public-key")
    public PublicKey publicKey(final JwtStateHelperProperties properties) {
        return KeyHelper.generateKey(
                getKeyFactory(),
                properties.getPublicKey(),
                X509EncodedKeySpec::new,
                KeyFactory::generatePublic
        );
    }

    /**
     * Builds a {@link PrivateKey} from the given {@code encoded} {@link String} representation of the key.
     *
     * @param properties The {@link JwtStateHelperProperties} from where the encoded private key is taken.
     * @return The created {@link PrivateKey}.
     */
    @Bean
    @Qualifier(value = "state-helper-private-key")
    public PrivateKey privateKey(final JwtStateHelperProperties properties) {
        return KeyHelper.generateKey(
                getKeyFactory(),
                properties.getPrivateKey(),
                PKCS8EncodedKeySpec::new,
                KeyFactory::generatePrivate
        );
    }


    /**
     * Retrieves a {@link KeyFactory} instance for the {@link AbstractJwtStateHelper#SIGNATURE_ALGORITHM} algorithm.
     *
     * @return The said {@link KeyFactory}.
     */
    private static KeyFactory getKeyFactory() {
        final var algorithm = AbstractJwtStateHelper.SIGNATURE_ALGORITHM.getFamilyName();
        try {
            return KeyFactory.getInstance(algorithm);
        } catch (final NoSuchAlgorithmException e) {
            LOGGER.error("Cannot get KeyFactory for algorithm {}", algorithm);
            LOGGER.debug("NoSuchAlgorithmException message {}", e.getMessage());
            LOGGER.trace("Stacktrace: ", e);
            throw new RuntimeException("Cannot get a KeyFactory instance for algorithm " + algorithm);
        }
    }


    /**
     * Properties needed to configure an {@link LtiStateHelper}.
     */
    @Data
    @ConfigurationProperties(prefix = "lti-service.app.state-helper")
    static class JwtStateHelperProperties {
        /**
         * An RSA public key in {@link String} format (used to verify states).
         */
        private String publicKey;
        /**
         * An RSA private key in {@link String} format (used to sign states).
         */
        private String privateKey;
    }
}
