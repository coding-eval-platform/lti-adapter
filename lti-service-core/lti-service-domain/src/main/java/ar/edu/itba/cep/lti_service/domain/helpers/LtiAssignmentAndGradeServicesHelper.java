package ar.edu.itba.cep.lti_service.domain.helpers;

import ar.edu.itba.cep.lti_service.services.LtiBadRequestException;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Component in charge of aiding with Assignment and Grade Services tasks.
 */
@Component
public class LtiAssignmentAndGradeServicesHelper {

    public static final String SCORE_SCOPE = "https://purl.imsglobal.org/spec/lti-ags/scope/score";

    private static final String LINE_ITEMS_PROPERTY = "lineitems";
    private static final String LINE_ITEM_PROPERTY = "lineitem";
    private static final String SCOPE_PROPERTY = "scope";


    /**
     * Builds a {@link AssignmentAndGradeServicesCapabilities} instance from the given {@code ltiMessage}.
     *
     * @param ltiMessage The LTI message from where the data to build the {@link AssignmentAndGradeServicesCapabilities}
     *                   instance is taken.
     * @return The created {@link AssignmentAndGradeServicesCapabilities}.
     * @throws RuntimeException If the given {@code ltiMessage} does not contain valid data to create the
     *                          {@link AssignmentAndGradeServicesCapabilities} instance.
     */
    public AssignmentAndGradeServicesCapabilities extractCapabilities(final Map<String, Object> ltiMessage) {
        return Optional.ofNullable(ltiMessage.get(LtiConstants.LtiClaims.AGS_CAPABILITIES))
                .filter(Map.class::isInstance)
                .map(Map.class::cast)
                .map(LtiAssignmentAndGradeServicesHelper::buildFromCapabilities)
                .orElseThrow(() -> new LtiBadRequestException("The capabilities claim must be a Map"))
                ;
    }


    /**
     * Builds a {@link AssignmentAndGradeServicesCapabilities} from the given {@code capabilities} {@link Map}.
     *
     * @param capabilities The {@link Map} from where the data must be extracted.
     * @return The created {@link AssignmentAndGradeServicesCapabilities}.
     * @apiNote This method does not perform any check over the created {@link AssignmentAndGradeServicesCapabilities}.
     */
    private static AssignmentAndGradeServicesCapabilities buildFromCapabilities(final Map<Object, Object> capabilities) {
        final var builder = AssignmentAndGradeServicesCapabilities.builder();
        LtiMessageHelper.extractStrings(capabilities, SCOPE_PROPERTY).ifPresent(builder::scopes);
        LtiMessageHelper.extractString(capabilities, LINE_ITEMS_PROPERTY).ifPresent(builder::lineItems);
        LtiMessageHelper.extractString(capabilities, LINE_ITEM_PROPERTY).ifPresent(builder::lineItem);

        return builder.build();
    }


    /**
     * Bean class containing stuff in a Assignment and Grade Services LTI message Claim.
     */
    @Getter
    @Builder
    @ToString(doNotUseGetters = true)
    @EqualsAndHashCode(doNotUseGetters = true)
    public static final class AssignmentAndGradeServicesCapabilities {

        private final String lineItems;
        private final String lineItem;
        private final List<String> scopes;
    }
}
