package ar.edu.itba.cep.lti_service.domain.helpers;

import ar.edu.itba.cep.lti_service.models.ToolDeployment;
import io.jsonwebtoken.*;
import lombok.Data;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Optional;
import java.util.UUID;

import static ar.edu.itba.cep.lti_service.domain.helpers.ExamCreationStateHelper.ExamCreationStateData;
import static ar.edu.itba.cep.lti_service.domain.helpers.ExamCreationStateHelper.ExamCreationStateDataJws;

/**
 * Component in charge of helping with LTI Message state tasks
 * (i.e encoding and decoding, applying the needed validations).
 */
public class ExamCreationStateHelper extends AbstractJwtStateHelper<ExamCreationStateData, ExamCreationStateDataJws> {


    private static final String RETURN_URL_CLAIM = "return_url";
    private static final String DATA_CLAIM = "data";
    private static final String TOOL_DEPLOYMENT_ID_CLAIM = "tool_deployment_id";
    private static final String NONCE_CLAIM = "nonce";


    /**
     * Constructor.
     *
     * @param publicKey  The {@link PublicKey} passed to the super constructor.
     * @param privateKey The {@link PrivateKey} passed to the super constructor.
     */
    public ExamCreationStateHelper(final PublicKey publicKey, final PrivateKey privateKey) {
        super(publicKey, privateKey, ExamCreationStateHelper::claimsSetter, StateDataJwtHandlerAdapter::getInstance);
    }


    /**
     * Convenient static method to be passed to the super constructor using method reference as a
     * {@link java.util.function.BiFunction} of {@link JwtBuilder} and {@link ExamCreationStateData},
     * returning a {@link JwtBuilder},
     * in which the {@link JwtBuilder} is configured using information in the {@link ExamCreationStateData},
     * and then returned again.
     *
     * @param builder The {@link JwtBuilder} to be configured.
     * @param state   The {@link ExamCreationStateData} from where information will be extracted.
     * @return The given {@code builder}, but configured with information in the given {@code state}.
     */
    private static JwtBuilder claimsSetter(final JwtBuilder builder, final ExamCreationStateData state) {
        builder
                .claim(RETURN_URL_CLAIM, state.getReturnUrl())
                .claim(TOOL_DEPLOYMENT_ID_CLAIM, state.getToolDeploymentId())
                .claim(NONCE_CLAIM, state.getNonce())
        ;
        Optional.ofNullable(state.getData()).ifPresent(value -> builder.claim(DATA_CLAIM, value)); // can be null
        return builder;
    }


    /**
     * Bean class representing an exam creation state.
     */
    @Data(staticConstructor = "create")
    public static final class ExamCreationStateData {
        /**
         * The URL to which the UA must be redirected when the Deep Linking Flow finishes.
         */
        private final String returnUrl;
        /**
         * The data sent by the LMS, which must be sent back.
         */
        private final String data;
        /**
         * The id of the {@link ToolDeployment} representing the
         * integration between the LMS and this tool, used to handle the reception of the LTI message.
         */
        private final UUID toolDeploymentId;
        /**
         * A nonce needed to avoid replay attacks.
         */
        private final String nonce;
    }


    /**
     * An extension of an {@link AbstractJws} using {@link ExamCreationStateData} as the body.
     */
    static final class ExamCreationStateDataJws extends AbstractJws<ExamCreationStateData> {

        /**
         * Constructor.
         *
         * @param header    The {@link JwsHeader}.
         * @param body      The body (i.e an object of type {@code S}).
         * @param signature The {@link Jws} signature.
         */
        ExamCreationStateDataJws(final JwsHeader header, final ExamCreationStateData body, final String signature) {
            super(header, body, signature);
        }
    }


    /**
     * An extension of a {@link JwtHandlerAdapter} that builds JWT whose bodies are {@link ExamCreationStateDataJws}s.
     */
    private static final class StateDataJwtHandlerAdapter extends AbstractJwtHandlerAdapter<ExamCreationStateData, ExamCreationStateDataJws> {

        /**
         * The singleton instance.
         */
        private static final StateDataJwtHandlerAdapter singleton = new StateDataJwtHandlerAdapter();


        /**
         * Constructor.
         */
        private StateDataJwtHandlerAdapter() {
            super(ExamCreationStateDataJws::new, StateDataJwtHandlerAdapter::fromJws);
        }


        /**
         * Creates a {@link ExamCreationStateData} from the given {@code jws}.
         *
         * @param jws The {@link Jws} from where the {@link ExamCreationStateData} will be built.
         * @return The created {@link ExamCreationStateData}.
         */
        private static ExamCreationStateData fromJws(final Jws<Claims> jws) {
            return ExamCreationStateData.create(
                    jws.getBody().get(RETURN_URL_CLAIM, String.class),
                    jws.getBody().get(DATA_CLAIM, String.class),
                    extractUUID(jws, TOOL_DEPLOYMENT_ID_CLAIM),
                    jws.getBody().get(NONCE_CLAIM, String.class)
            );
        }


        /**
         * @return The singleton instance.
         */
        private static StateDataJwtHandlerAdapter getInstance() {
            return singleton;
        }
    }
}
