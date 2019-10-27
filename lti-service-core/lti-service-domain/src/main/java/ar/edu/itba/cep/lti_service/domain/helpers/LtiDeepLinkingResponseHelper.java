package ar.edu.itba.cep.lti_service.domain.helpers;

import ar.edu.itba.cep.lti_service.models.admin.ToolDeployment;
import lombok.Getter;
import lombok.Singular;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Component in charge of aiding with Deep Linking response messages tasks.
 */
@Component
public class LtiDeepLinkingResponseHelper {

    private static final String DEEP_LINKING_MESSAGE_TYPE = "LTIDeepLinkingResponse";


    public Map<String, Object> buildMessage(
            final ToolDeployment toolDeployment,
            final String data,
            final List<ContentItem> contentItems) {
        Assert.notNull(toolDeployment, "The tool deployment must not be null");
        Assert.notNull(contentItems, "The content items list must not be null");

        final Instant now = Instant.now();
        final Map<String, Object> ltiMessage = new HashMap<>();

        ltiMessage.put(LtiConstants.LtiClaims.ISSUER, toolDeployment.getClientId()); // Now the issuer is this tool
        ltiMessage.put(LtiConstants.LtiClaims.AUDIENCE, toolDeployment.getIssuer()); // And the audience is the platform
        ltiMessage.put(LtiConstants.LtiClaims.ISSUED_AT, now);
        ltiMessage.put(LtiConstants.LtiClaims.EXPIRATION, now.plus(Duration.ofMinutes(10)));
        ltiMessage.put(LtiConstants.LtiClaims.NONCE, UUID.randomUUID());
        ltiMessage.put(LtiConstants.LtiClaims.AUTHORIZED_PARTY, toolDeployment.getIssuer()); // Same as aud

        ltiMessage.put(LtiConstants.LtiClaims.DEPLOYMENT_ID, toolDeployment.getDeploymentId());
        ltiMessage.put(LtiConstants.LtiClaims.MESSAGE_TYPE, DEEP_LINKING_MESSAGE_TYPE);
        ltiMessage.put(LtiConstants.LtiClaims.VERSION, LtiConstants.LTI_VERSION);
        Optional.ofNullable(data).ifPresent(v -> ltiMessage.put(LtiConstants.LtiClaims.DL_DATA, v)); // Can be null
        ltiMessage.put(LtiConstants.LtiClaims.DL_CONTENT_ITEMS, contentItems);


        return ltiMessage;
    }

    /**
     * Interface used to mark class that can be used as Content Items in an LTI Deep Linking response.
     */
    public interface ContentItem {

    }

    /**
     * Represents an LTI Resource Link Content Item.
     */
    @Getter
    @lombok.Builder(builderClassName = "Builder")
    public static final class LtiResourceLink implements ContentItem {
        /**
         * The type of ContentItem
         */
        private static final String TYPE = "ltiResourceLink";

        private final String type = TYPE;
        private final String title;
        private final String text;
        private final String url;
        private final Image icon;
        private final Image thumbnail;
        private final Iframe iframe;
        @Singular("custom")
        private final Map<String, Object> custom;
        private final LineItem lineItem;
        private final Period available;
        private final Period submission;


        /**
         * Represents an image that can be sent as part of an LTI Resource Link (i.e icon and thumbnail).
         */
        @Getter
        @lombok.Builder(builderClassName = "Builder")
        public static final class Image {
            private final String url;
            private final Integer width;
            private final Integer height;
        }

        /**
         * Represents the iframe that can be sent as part of an LTI Resource Link.
         */
        @Getter
        @lombok.Builder(builderClassName = "Builder")
        public static final class Iframe {
            private final Integer width;
            private final Integer height;
        }


        /**
         * Represents the line item that can be sent as part of an LTI Resource Link.
         */
        @Getter
        @lombok.Builder(builderClassName = "Builder")
        public static final class LineItem {
            private final String label;
            private final int scoreMaximum;
            private final String resourceId;
            private final String tag;
        }

        /**
         * Represents a period that can be sent as part of an LTI Resource Link. (i.e available and submission).
         */
        @Getter
        @lombok.Builder(builderClassName = "Builder")
        public static final class Period {
            private final ZonedDateTime startDateTime;
            private final ZonedDateTime endDateTime;
        }
    }


//    /**
//     * Builds a {@link DeepLinkingSettings} from the given {@code ltiMessage}.
//     *
//     * @param ltiMessage The LTI message from where Deep Linking settings must be extracted.
//     * @return The created {@link DeepLinkingSettings}.
//     * @throws RuntimeException If the given {@code ltiMessage} is not a valid Deep Linking Message
//     *                          (i.e checks the message type and the Deep Linking Settings Claim).
//     */
//    public DeepLinkingSettings extractDeepLinkingSettings(final Map<String, Object> ltiMessage) {
//        validateDeepLinkingMessage(ltiMessage);
//        return doExtractSettings(ltiMessage);
//    }
//
//
//    /**
//     * Validates that the given {@code ltiMessage} is a Deep Linking Message.
//     *
//     * @param ltiMessage The LTI message to be validated.
//     * @throws RuntimeException If the given {@code ltiMessage} is not a Deep Linking Message.
//     */
//    private static void validateDeepLinkingMessage(final Map<String, Object> ltiMessage)
//            throws RuntimeException {
//        Optional.ofNullable(ltiMessage.get(LtiConstants.LtiClaims.MESSAGE_TYPE))
//                .filter(DEEP_LINKING_MESSAGE_TYPE::equals)
//                .orElseThrow(RuntimeException::new) // TODO: define new exception
//        ;
//    }
//
//    /**
//     * Builds a {@link DeepLinkingSettings} from the given {@code ltiMessage}.
//     *
//     * @param ltiMessage The LTI message from where Deep Linking settings must be extracted.
//     * @return The created {@link DeepLinkingSettings}.
//     * @throws RuntimeException If the given {@code ltiMessage} does not contain a valid Deep Linking Claim.
//     */
//    private static DeepLinkingSettings doExtractSettings(final Map<String, Object> ltiMessage) throws RuntimeException {
//        return Optional.ofNullable(ltiMessage.get(LtiConstants.LtiClaims.DL_SETTINGS))
//                .filter(settings -> settings instanceof Map)
//                .map(Map.class::cast)
//                .map(LtiDeepLinkingResponseHelper::buildFromSettings)
//                .filter(DeepLinkingSettings::requiredArePresent)
//                .orElseThrow(RuntimeException::new) // TODO: define new exception
//                ;
//    }
//
//    /**
//     * Builds a {@link DeepLinkingSettings} from the given {@code settings} {@link Map}.
//     *
//     * @param settings The {@link Map} from where the data must be extracted.
//     * @return The created {@link DeepLinkingSettings}.
//     * @apiNote This method does not perform any check over the created {@link DeepLinkingSettings}.
//     */
//    private static DeepLinkingSettings buildFromSettings(final Map<Object, Object> settings) {
//        final var builder = DeepLinkingSettings.builder();
//        extractString(settings, RETURN_URL_PROPERTY).ifPresent(builder::returnUrl);
//        extractStrings(settings, ACCEPT_TYPES_PROPERTY)
//                .stream()
//                .flatMap(Collection::stream)
//                .map(DeepLinkingContentType::fromString)
//                .collect(
//                        Collector.of(
//                                LinkedList<DeepLinkingContentType>::new,
//                                List::add,
//                                (l1, l2) -> {
//                                    l1.addAll(l2);
//                                    return l1;
//                                },
//                                Optional::of,
//                                Collector.Characteristics.CONCURRENT,
//                                Collector.Characteristics.UNORDERED
//                        )
//                )
//                .ifPresent(builder::acceptTypes);
//        extractStrings(settings, ACCEPTABLE_PRESENTATION_DOCUMENT_TARGETS_PROPERTY)
//                .ifPresent(builder::acceptablePresentationDocumentTargets);
//        extractString(settings, ACCEPT_MEDIA_TYPES_PROPERTY).ifPresent(builder::acceptMediaTypes);
//        extractBoolean(settings, ACCEPT_MULTIPLE_PROPERTY).ifPresent(builder::acceptMultiple);
//        extractBoolean(settings, AUTO_CREATE_PROPERTY).ifPresent(builder::autoCreate);
//        extractString(settings, TITLE_PROPERTY).ifPresent(builder::title);
//        extractString(settings, TEXT_URL_PROPERTY).ifPresent(builder::text);
//        extractString(settings, DATA_URL_PROPERTY).ifPresent(builder::data);
//
//        return builder.build();
//    }
//
//
//    /**
//     * Extracts a {@link String} from the given {@code settings}, whose key is the given {@code property}.
//     *
//     * @param settings The {@link Map} from where the value must be extracted.
//     * @param property The property name to be extracted (i.e the {@link Map}'s key).
//     * @return An {@link Optional} holding the extracted value if present,
//     * and if it is an instance of {@link String}, or empty otherwise.
//     */
//    private static Optional<String> extractString(final Map<Object, Object> settings, final String property) {
//        return extract(settings, property, String.class);
//    }
//
//
//    /**
//     * Extracts a {@link Boolean} from the given {@code settings}, whose key is the given {@code property}.
//     *
//     * @param settings The {@link Map} from where the value must be extracted.
//     * @param property The property name to be extracted (i.e the {@link Map}'s key).
//     * @return An {@link Optional} holding the extracted value if present,
//     * and if it is an instance of {@link Boolean}, or empty otherwise.
//     */
//    private static Optional<Boolean> extractBoolean(final Map<Object, Object> settings, final String property) {
//        return extract(settings, property, Boolean.class);
//    }
//
//    /**
//     * Extracts a {@link List} of {@link String} from the given {@code settings}, whose key is the given {@code property}.
//     *
//     * @param settings The {@link Map} from where the value must be extracted.
//     * @param property The property name to be extracted (i.e the {@link Map}'s key).
//     * @return An {@link Optional} holding the extracted {@link List} of {@link String} if present,
//     * and if it is an instance of {@link Collection}, and if inside the said {@link Collection},
//     * all elements are {@link String}, or empty otherwise.
//     */
//    private static Optional<List<String>> extractStrings(final Map<Object, Object> settings, final String property) {
//        return extract(settings, property, Collection.class)
//                .map(val -> (Collection<?>) val)
//                .filter(c -> c.stream().allMatch(obj -> obj instanceof String))
//                .map(c -> c.stream().map(Object::toString).collect(Collectors.toList()))
//                ;
//    }
//
//
//    /**
//     * Extracts a value of type {@code T} from the given {@code settings} map, using the given {@code property} as a key.
//     *
//     * @param settings The {@link Map} from where the value must be extracted.
//     * @param property The property name to be extracted (i.e the {@link Map}'s key).
//     * @param klass    The class of the value to be extracted.
//     * @param <T>      The concrete type of the value to be extracted.
//     * @return An {@link Optional} holding the extracted value if present,
//     * and if it is an instance of the given {@code klass}, or empty otherwise.
//     */
//    private static <T> Optional<T> extract(final Map<Object, Object> settings, final String property, final Class<T> klass) {
//        return Optional.ofNullable(settings.get(property)).filter(klass::isInstance).map(klass::cast);
//    }
//
//
//    /**
//     * Bean class containing stuff in a Deep Linking Settings LTI message Claim.
//     */
//    @Getter
//    @Builder
//    @ToString(doNotUseGetters = true)
//    @EqualsAndHashCode(doNotUseGetters = true)
//    public static final class DeepLinkingSettings {
//
//        private final String returnUrl;
//        private final List<DeepLinkingContentType> acceptTypes;
//        private final List<String> acceptablePresentationDocumentTargets;
//        private final String acceptMediaTypes;
//        private final Boolean acceptMultiple;
//        private final Boolean autoCreate;
//        private final String title;
//        private final String text;
//        private final String data;
//
//        /**
//         * Checks whether required stuff is present (i.e is not {@code null}).
//         *
//         * @return {@code true} if all required stuff is present, or {@code false} otherwise.
//         */
//        private boolean requiredArePresent() {
//            return returnUrl != null && acceptTypes != null && acceptablePresentationDocumentTargets != null;
//        }
//    }
//
//    @AllArgsConstructor
//    @Getter(AccessLevel.PRIVATE)
//    public enum DeepLinkingContentType {
//        LINK("link"),
//        LTI_RESOURCE_LINK("ltiResourceLink"),
//        FILE("file"),
//        HTML("html"),
//        IMAGE("image"),
//        OTHER("other"),
//        ;
//
//        /**
//         * The name given to the content type by the Deep Linking spec.
//         */
//        private final String specName;
//
//        /**
//         * A {@link Map} that contain each {@link DeepLinkingSettings}, index by their {@code specName}.
//         */
//        private static final Map<String, DeepLinkingContentType> MAP = Arrays.stream(DeepLinkingContentType.values())
//                .collect(Collectors.toMap(DeepLinkingContentType::getSpecName, Function.identity()));
//
//        /**
//         * Retrieves the {@link DeepLinkingContentType} corresponding to the given {@code str}.
//         *
//         * @param str The {@link String} representation of the {@link DeepLinkingContentType} to be returned.
//         * @return The {@link DeepLinkingContentType} corresponding to the given {@code str}, if there is such,
//         * or {@link DeepLinkingContentType#OTHER} otherwise.
//         */
//        private static DeepLinkingContentType fromString(final String str) {
//            return MAP.getOrDefault(str, OTHER);
//        }
//
//    }
}
