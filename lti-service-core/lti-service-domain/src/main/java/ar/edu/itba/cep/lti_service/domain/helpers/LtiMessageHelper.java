package ar.edu.itba.cep.lti_service.domain.helpers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Class containing several methods that helps with the treatment of lti messages.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
/* package */ final class LtiMessageHelper {

    /**
     * Extracts a {@link String} from the given {@code settings}, whose key is the given {@code property}.
     *
     * @param settings The {@link Map} from where the value must be extracted.
     * @param property The property name to be extracted (i.e the {@link Map}'s key).
     * @return An {@link Optional} holding the extracted value if present,
     * and if it is an instance of {@link String}, or empty otherwise.
     */
    /* package */
    static Optional<String> extractString(final Map<Object, Object> settings, final String property) {
        return extract(settings, property, String.class);
    }


    /**
     * Extracts a {@link Boolean} from the given {@code settings}, whose key is the given {@code property}.
     *
     * @param settings The {@link Map} from where the value must be extracted.
     * @param property The property name to be extracted (i.e the {@link Map}'s key).
     * @return An {@link Optional} holding the extracted value if present,
     * and if it is an instance of {@link Boolean}, or empty otherwise.
     */
    /* package */
    static Optional<Boolean> extractBoolean(final Map<Object, Object> settings, final String property) {
        return extract(settings, property, Boolean.class);
    }

    /**
     * Extracts a {@link List} of {@link String} from the given {@code settings}, whose key is the given {@code property}.
     *
     * @param settings The {@link Map} from where the value must be extracted.
     * @param property The property name to be extracted (i.e the {@link Map}'s key).
     * @return An {@link Optional} holding the extracted {@link List} of {@link String} if present,
     * and if it is an instance of {@link Collection}, and if inside the said {@link Collection},
     * all elements are {@link String}, or empty otherwise.
     */
    /* package */
    static Optional<List<String>> extractStrings(final Map<Object, Object> settings, final String property) {
        return extract(settings, property, Collection.class)
                .map(val -> (Collection<?>) val)
                .filter(c -> c.stream().allMatch(obj -> obj instanceof String))
                .map(c -> c.stream().map(Object::toString).collect(Collectors.toList()))
                ;
    }


    /**
     * Extracts a value of type {@code T} from the given {@code settings} map, using the given {@code property} as a key.
     *
     * @param settings The {@link Map} from where the value must be extracted.
     * @param property The property name to be extracted (i.e the {@link Map}'s key).
     * @param klass    The class of the value to be extracted.
     * @param <T>      The concrete type of the value to be extracted.
     * @return An {@link Optional} holding the extracted value if present,
     * and if it is an instance of the given {@code klass}, or empty otherwise.
     */
    /* package */
    static <T> Optional<T> extract(final Map<Object, Object> settings, final String property, final Class<T> klass) {
        return Optional.ofNullable(settings.get(property)).filter(klass::isInstance).map(klass::cast);
    }
}
