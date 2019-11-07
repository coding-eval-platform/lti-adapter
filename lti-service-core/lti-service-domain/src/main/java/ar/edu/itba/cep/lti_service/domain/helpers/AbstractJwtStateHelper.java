package ar.edu.itba.cep.lti_service.domain.helpers;

import ar.edu.itba.cep.lti.LtiAuthenticationException;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.util.StringUtils;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Abstract JWT state helper. It can encode/decode objects of type {@code S} into JWT
 * (i.e in JWS format of type {@code S}).
 *
 * @param <S> The concrete type of body to be set in the resultant {@link Jws}.
 * @param <J> The concrete type of subclass of {@link AbstractJws}.
 */
@AllArgsConstructor
public abstract class AbstractJwtStateHelper<S, J extends AbstractJwtStateHelper.AbstractJws<S>> {

    /**
     * Signature algorithm used to sign the jwt.
     */
    public static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.RS512;


    /**
     * The {@link PublicKey} used to verify signatures.
     */
    private final PublicKey publicKey;
    /**
     * The {@link PrivateKey} used to sign the state.
     */
    private final PrivateKey privateKey;

    /**
     * A {@link BiFunction} of {@link JwtBuilder} and {@code S}, that returns {@link JwtBuilder},
     * which will be used to configure an initial {@link JwtBuilder}, when encoding.
     */
    private final BiFunction<JwtBuilder, S, JwtBuilder> claimsSetters;

    /**
     * A {@link Supplier} of {@link AbstractJwtHandlerAdapter} of {@code S} and {@code J},
     * used to get an instance of a {@link AbstractJwtHandlerAdapter} when decoding.
     */
    private final Supplier<AbstractJwtHandlerAdapter<S, J>> jwtHandlerSupplier;


    /**
     * Encodes the given {@code decoded} object (into a JWT).
     *
     * @param decoded The object of type {@code S} to be encoded.
     * @return An encoded form of the given {@code decoded} object (in JWT format).
     */
    public String encode(final S decoded) {
        return claimsSetters.apply(Jwts.builder(), decoded)
                .signWith(privateKey, SIGNATURE_ALGORITHM)
                .compact()
                ;
    }

    /**
     * Decoded the given {@code encoded} {@link String} (that is a JWT).
     *
     * @param encoded The {@link String} to be decoded.
     * @return The object of type {@code S} decoded from the given {@code encoded} {@link String} (that is a JWT).
     */
    public S decode(final String encoded) {
        try {
            return Jwts.parser()
                    .setSigningKey(publicKey)
                    .parse(encoded, jwtHandlerSupplier.get())
                    .getBody();
        } catch (final JwtException e) {
            throw new LtiAuthenticationException("The state could not be parsed", e);
        }
    }


    /**
     * An extension of a {@link JwtHandlerAdapter} that maps a {@link Jws} of {@link Claims} into
     * a {@link Jws} of type {@code S}.
     *
     * @param <S> The concrete type of body to be set in the resultant {@link Jws}.
     * @param <J> The concrete type of subclass of {@link AbstractJws}.
     */
    @AllArgsConstructor
    static abstract class AbstractJwtHandlerAdapter<S, J extends AbstractJws<S>> extends JwtHandlerAdapter<Jws<S>> {

        /**
         * An {@link AbstractJwsCreator} needed to instantiate the {@link AbstractJws}.
         */
        private final AbstractJwsCreator<S, J> abstractJwsCreator;
        /**
         * A {@link Function} that maps a {@link Jws} of {@link Claims} into an object of type {@code S}
         * (i.e to be set in the resultant {@link AbstractJws}). The entire {@link Jws} is needed as the
         * {@link JwsHeader} might be used to throw a {@link JwtException}.
         */
        private final Function<Jws<Claims>, S> bodyMapper;

        @Override
        public Jws<S> onClaimsJws(final Jws<Claims> jws) {
            return abstractJwsCreator.build(jws.getHeader(), bodyMapper.apply(jws), jws.getSignature());
        }

        /**
         * A functional interface that defines a method to build an {@link AbstractJws} from a {@link JwsHeader},
         * a body of type {@code S}, and a signature.
         *
         * @param <S> The concrete type of body to be set in the resultant {@link Jws}.
         * @param <J> The concrete type of subclass of {@link AbstractJws}.
         */
        @FunctionalInterface
        interface AbstractJwsCreator<S, J extends AbstractJws<S>> {
            /**
             * Builds an {@link AbstractJws} from the given arguments.
             *
             * @param jwsHeader The {@link JwsHeader}.
             * @param body      The body.
             * @param signature The signature.
             * @return The created {@link AbstractJws}.
             */
            J build(final JwsHeader jwsHeader, final S body, final String signature);
        }

        /**
         * Extracts an {@link UUID} from the given {@code jws}.
         *
         * @param jws The {@link Jws} from where the {@link UUID} must be extracted (using the {code claimName} Claim).
         * @return The extracted {@link UUID}.
         * @throws MissingClaimException If the {@code claimName} Claim is not present.
         * @throws MalformedJwtException If the {@code claimName} Claim does not contain a valid {@link UUID}.
         */
        protected static UUID extractUUID(final Jws<Claims> jws, final String claimName)
                throws MissingClaimException, MalformedJwtException {
            final var claims = jws.getBody();
            final var id = Optional.ofNullable(claims.get(claimName, String.class))
                    .filter(StringUtils::hasLength)
                    .orElseThrow(() -> new MissingClaimException(
                            jws.getHeader(),
                            claims,
                            "Missing \"" + claimName + "\" claim")
                    );
            try {
                return UUID.fromString(id);
            } catch (final IllegalArgumentException e) {
                throw new MalformedJwtException("The \"" + claimName + "\" claim must be a valid UUID", e);
            }
        }
    }

    /**
     * An abstract implementation of a {@link Jws} whose body is an object of type {@code S}.
     *
     * @param <S> The concrete type of object in the {@link Jws}.
     */
    @AllArgsConstructor
    @ToString(doNotUseGetters = true)
    @EqualsAndHashCode(doNotUseGetters = true)
    static abstract class AbstractJws<S> implements Jws<S> {
        /**
         * The {@link JwsHeader}.
         */
        private final JwsHeader header;
        /**
         * The body (i.e an object of type {@code S}).
         */
        private final S body;
        /**
         * The {@link Jws} signature.
         */
        private final String signature;


        @Override
        public JwsHeader getHeader() {
            return header;
        }

        @Override
        public S getBody() {
            return body;
        }

        @Override
        public String getSignature() {
            return signature;
        }
    }
}
