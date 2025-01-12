package ar.edu.itba.cep.lti_service.domain.helpers;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

/**
 * Class containing constants related to the LTI 1.3.0 spec.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class LtiConstants {

    /**
     * The LTI version supported by this tool.
     */
    public static final String LTI_VERSION = "1.3.0";


    /**
     * Class containing some of the LTI message claims as constants.
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class LtiClaims {

        // IMS Security Framework claims
        public static final String ISSUER = "iss";
        public static final String AUDIENCE = "aud";
        public static final String SUBJECT = "sub";
        public static final String EXPIRATION = "exp";
        public static final String ISSUED_AT = "iat";
        public static final String NONCE = "nonce";
        public static final String AUTHORIZED_PARTY = "azp";

        // General LTI message claims
        public static final String MESSAGE_TYPE = "https://purl.imsglobal.org/spec/lti/claim/message_type";
        public static final String VERSION = "https://purl.imsglobal.org/spec/lti/claim/version";
        public static final String DEPLOYMENT_ID = "https://purl.imsglobal.org/spec/lti/claim/deployment_id";
        public static final String TARGET_LINK_URI = "https://purl.imsglobal.org/spec/lti/claim/target_link_uri";
        public static final String RESOURCE_LINK = "https://purl.imsglobal.org/spec/lti/claim/resource_link";
        public static final String CUSTOM = "https://purl.imsglobal.org/spec/lti/claim/custom";
        public static final String LAUNCH_PRESENTATION = "https://purl.imsglobal.org/spec/lti/claim/launch_presentation";

        // User identification and customization claims
        public static final String GIVEN_NAME = "given_name";
        public static final String MIDDLE_NAME = "middle_name";
        public static final String FAMILY_NAME = "family_name";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String ROLES = "https://purl.imsglobal.org/spec/lti/claim/roles";
        public static final String LOCALE = "locale";


        // Deep linking stuff
        public static final String DL_SETTINGS = "https://purl.imsglobal.org/spec/lti-dl/claim/deep_linking_settings";
        public static final String DL_CONTENT_ITEMS = "https://purl.imsglobal.org/spec/lti-dl/claim/content_items";
        public static final String DL_DATA = "https://purl.imsglobal.org/spec/lti-dl/claim/data";

        // Assignment and Grade Services stuff
        public static final String AGS_CAPABILITIES = "https://purl.imsglobal.org/spec/lti-ags/claim/endpoint";
    }
}
