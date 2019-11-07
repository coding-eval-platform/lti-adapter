package ar.edu.itba.cep.lti_service.domain.helpers;

import ar.edu.itba.cep.lti_service.services.LtiBadRequestException;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Component in charge of aiding with Deep Linking request messages tasks.
 */
@Component
public class LtiDeepLinkingRequestHelper {

    private static final String DEEP_LINKING_MESSAGE_TYPE = "LtiDeepLinkingRequest";

    private static final String RETURN_URL_PROPERTY = "deep_link_return_url";
    private static final String ACCEPT_TYPES_PROPERTY = "accept_types";
    private static final String ACCEPTABLE_PRESENTATION_DOCUMENT_TARGETS_PROPERTY = "accept_presentation_document_targets";
    private static final String ACCEPT_MEDIA_TYPES_PROPERTY = "accept_media_types";
    private static final String ACCEPT_MULTIPLE_PROPERTY = "accept_multiple";
    private static final String AUTO_CREATE_PROPERTY = "auto_create";
    private static final String TITLE_PROPERTY = "title";
    private static final String TEXT_URL_PROPERTY = "text";
    private static final String DATA_URL_PROPERTY = "data";


    /**
     * Builds a {@link DeepLinkingSettings} from the given {@code ltiMessage}.
     *
     * @param ltiMessage The LTI message from where Deep Linking settings must be extracted.
     * @return The created {@link DeepLinkingSettings}.
     * @throws RuntimeException If the given {@code ltiMessage} is not a valid Deep Linking Message
     *                          (i.e checks the message type and the Deep Linking Settings Claim).
     */
    public DeepLinkingSettings extractDeepLinkingSettings(final Map<String, Object> ltiMessage) {
        validateDeepLinkingMessage(ltiMessage);
        return doExtractSettings(ltiMessage);
    }


    /**
     * Validates that the given {@code ltiMessage} is a Deep Linking Message.
     *
     * @param ltiMessage The LTI message to be validated.
     * @throws RuntimeException If the given {@code ltiMessage} is not a Deep Linking Message.
     */
    private static void validateDeepLinkingMessage(final Map<String, Object> ltiMessage)
            throws RuntimeException {
        Optional.ofNullable(ltiMessage.get(LtiConstants.LtiClaims.MESSAGE_TYPE))
                .filter(DEEP_LINKING_MESSAGE_TYPE::equals)
                .orElseThrow(
                        () -> new LtiBadRequestException(
                                "The LTI message type must be \"" + DEEP_LINKING_MESSAGE_TYPE + "\""
                        )
                )
        ;
    }

    /**
     * Builds a {@link DeepLinkingSettings} from the given {@code ltiMessage}.
     *
     * @param ltiMessage The LTI message from where Deep Linking settings must be extracted.
     * @return The created {@link DeepLinkingSettings}.
     * @throws RuntimeException If the given {@code ltiMessage} does not contain a valid Deep Linking Claim.
     */
    private static DeepLinkingSettings doExtractSettings(final Map<String, Object> ltiMessage) throws RuntimeException {
        return Optional.ofNullable(ltiMessage.get(LtiConstants.LtiClaims.DL_SETTINGS))
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(LtiDeepLinkingRequestHelper::buildFromSettings)
                .filter(DeepLinkingSettings::requiredArePresent)
                .orElseThrow(
                        () -> new LtiBadRequestException(
                                "The settings claim must be a Map containing at least the following keys: " +
                                        "\"" + RETURN_URL_PROPERTY + "\", " +
                                        "\"" + ACCEPT_TYPES_PROPERTY + "\", " +
                                        "\"" + ACCEPTABLE_PRESENTATION_DOCUMENT_TARGETS_PROPERTY + "\""
                        )
                )
                ;
    }

    /**
     * Builds a {@link DeepLinkingSettings} from the given {@code settings} {@link Map}.
     *
     * @param settings The {@link Map} from where the data must be extracted.
     * @return The created {@link DeepLinkingSettings}.
     * @apiNote This method does not perform any check over the created {@link DeepLinkingSettings}.
     */
    private static DeepLinkingSettings buildFromSettings(final Map<Object, Object> settings) {
        final var builder = DeepLinkingSettings.builder();
        LtiMessageHelper.extractString(settings, RETURN_URL_PROPERTY).ifPresent(builder::returnUrl);
        LtiMessageHelper.extractStrings(settings, ACCEPT_TYPES_PROPERTY)
                .stream()
                .flatMap(Collection::stream)
                .map(DeepLinkingContentType::fromString)
                .collect(
                        Collector.of(
                                LinkedList<DeepLinkingContentType>::new,
                                List::add,
                                (l1, l2) -> {
                                    l1.addAll(l2);
                                    return l1;
                                },
                                Optional::of,
                                Collector.Characteristics.CONCURRENT,
                                Collector.Characteristics.UNORDERED
                        )
                )
                .ifPresent(builder::acceptTypes);
        LtiMessageHelper.extractStrings(settings, ACCEPTABLE_PRESENTATION_DOCUMENT_TARGETS_PROPERTY)
                .ifPresent(builder::acceptablePresentationDocumentTargets);
        LtiMessageHelper.extractString(settings, ACCEPT_MEDIA_TYPES_PROPERTY).ifPresent(builder::acceptMediaTypes);
        LtiMessageHelper.extractBoolean(settings, ACCEPT_MULTIPLE_PROPERTY).ifPresent(builder::acceptMultiple);
        LtiMessageHelper.extractBoolean(settings, AUTO_CREATE_PROPERTY).ifPresent(builder::autoCreate);
        LtiMessageHelper.extractString(settings, TITLE_PROPERTY).ifPresent(builder::title);
        LtiMessageHelper.extractString(settings, TEXT_URL_PROPERTY).ifPresent(builder::text);
        LtiMessageHelper.extractString(settings, DATA_URL_PROPERTY).ifPresent(builder::data);

        return builder.build();
    }


    /**
     * Bean class containing stuff in a Deep Linking Settings LTI message Claim.
     */
    @Getter
    @Builder
    @ToString(doNotUseGetters = true)
    @EqualsAndHashCode(doNotUseGetters = true)
    public static final class DeepLinkingSettings {

        private final String returnUrl;
        private final List<DeepLinkingContentType> acceptTypes;
        private final List<String> acceptablePresentationDocumentTargets;
        private final String acceptMediaTypes;
        private final Boolean acceptMultiple;
        private final Boolean autoCreate;
        private final String title;
        private final String text;
        private final String data;

        /**
         * Checks whether required stuff is present (i.e is not {@code null}).
         *
         * @return {@code true} if all required stuff is present, or {@code false} otherwise.
         */
        private boolean requiredArePresent() {
            return returnUrl != null && acceptTypes != null && acceptablePresentationDocumentTargets != null;
        }
    }

    @AllArgsConstructor
    @Getter(AccessLevel.PRIVATE)
    public enum DeepLinkingContentType {
        LINK("link"),
        LTI_RESOURCE_LINK("ltiResourceLink"),
        FILE("file"),
        HTML("html"),
        IMAGE("image"),
        OTHER("other"),
        ;

        /**
         * The name given to the content type by the Deep Linking spec.
         */
        private final String specName;

        /**
         * A {@link Map} that contain each {@link DeepLinkingSettings}, index by their {@code specName}.
         */
        private static final Map<String, DeepLinkingContentType> MAP = Arrays.stream(DeepLinkingContentType.values())
                .collect(Collectors.toMap(DeepLinkingContentType::getSpecName, Function.identity()));

        /**
         * Retrieves the {@link DeepLinkingContentType} corresponding to the given {@code str}.
         *
         * @param str The {@link String} representation of the {@link DeepLinkingContentType} to be returned.
         * @return The {@link DeepLinkingContentType} corresponding to the given {@code str}, if there is such,
         * or {@link DeepLinkingContentType#OTHER} otherwise.
         */
        private static DeepLinkingContentType fromString(final String str) {
            return MAP.getOrDefault(str, OTHER);
        }

    }
}
