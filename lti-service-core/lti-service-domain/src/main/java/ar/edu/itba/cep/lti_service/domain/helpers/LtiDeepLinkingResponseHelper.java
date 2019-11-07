package ar.edu.itba.cep.lti_service.domain.helpers;

import ar.edu.itba.cep.lti_service.models.ToolDeployment;
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
}
