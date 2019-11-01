package ar.edu.itba.cep.lti_service.domain.helpers;

import ar.edu.itba.cep.lti_service.models.ToolDeployment;
import io.jsonwebtoken.*;
import lombok.Data;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.UUID;

import static ar.edu.itba.cep.lti_service.domain.helpers.LtiStateHelper.StateData;
import static ar.edu.itba.cep.lti_service.domain.helpers.LtiStateHelper.StateDataJws;

/**
 * Component in charge of helping with LTI Message state tasks
 * (i.e encoding and decoding, applying the needed validations).
 */
public class LtiStateHelper extends AbstractJwtStateHelper<StateData, StateDataJws> {


    private static final String TOOL_DEPLOYMENT_ID_CLAIM = "tool_deployment_id";
    private static final String NONCE_CLAIM = "nonce";


    /**
     * Constructor.
     *
     * @param publicKey  The {@link PublicKey} passed to the super constructor.
     * @param privateKey The {@link PrivateKey} passed to the super constructor.
     */
    public LtiStateHelper(final PublicKey publicKey, final PrivateKey privateKey) {
        super(publicKey, privateKey, LtiStateHelper::claimsSetter, StateDataJwtHandlerAdapter::getInstance);
    }


    /**
     * Convenient static method to be passed to the super constructor using method reference as a
     * {@link java.util.function.BiFunction} of {@link JwtBuilder} and {@link StateData},
     * returning a {@link JwtBuilder},
     * in which the {@link JwtBuilder} is configured using information in the {@link StateData},
     * and then returned again.
     *
     * @param builder The {@link JwtBuilder} to be configured.
     * @param data    The {@link StateData} from where information will be extracted.
     * @return The given {@code builder}, but configured with information in the given {@code data}.
     */
    private static JwtBuilder claimsSetter(final JwtBuilder builder, final StateData data) {
        return builder.claim(TOOL_DEPLOYMENT_ID_CLAIM, data.getToolDeploymentId()).claim(NONCE_CLAIM, data.getNonce());
    }


    /**
     * Bean class representing an LTI message state.
     */
    @Data(staticConstructor = "create")
    public static final class StateData {
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
     * An extension of an {@link AbstractJwtStateHelper.AbstractJws} using {@link StateData} as the body.
     */
    static final class StateDataJws extends AbstractJws<StateData> {

        /**
         * Constructor.
         *
         * @param header    The {@link JwsHeader}.
         * @param body      The body (i.e an object of type {@code S}).
         * @param signature The {@link Jws} signature.
         */
        StateDataJws(final JwsHeader header, final StateData body, final String signature) {
            super(header, body, signature);
        }
    }


    /**
     * An extension of a {@link JwtHandlerAdapter} that builds JWT whose bodies are {@link StateDataJws}s.
     */
    private static final class StateDataJwtHandlerAdapter extends AbstractJwtHandlerAdapter<StateData, StateDataJws> {

        /**
         * The singleton instance.
         */
        private static final StateDataJwtHandlerAdapter singleton = new StateDataJwtHandlerAdapter();


        /**
         * Constructor.
         */
        private StateDataJwtHandlerAdapter() {
            super(StateDataJws::new, StateDataJwtHandlerAdapter::fromJws);
        }


        /**
         * Creates a {@link StateData} from the given {@code jws}.
         *
         * @param jws The {@link Jws} from where the {@link StateData} will be built.
         * @return The created {@link StateData}.
         */
        private static StateData fromJws(final Jws<Claims> jws) {
            return StateData.create(
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
