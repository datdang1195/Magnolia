package vn.ekino.certificate.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@UtilityClass
@Slf4j
public class CommonUtils {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    static {
        MAPPER.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false);
        MAPPER.registerModule(new JavaTimeModule());
    }

    public static <T> Stream<T> streamOf(List<T> list) {
        return ofNullableList(list).stream();
    }

    public static <T> Stream<T> streamOf(Set<T> set) {
        return ofNullableSet(set).stream();
    }

    public static <T> List<T> ofNullableList(List<T> list) {
        return Optional.ofNullable(list)
                .orElse(Collections.emptyList());
    }

    public static <T> Set<T> ofNullableSet(Set<T> set) {
        return Optional.ofNullable(set)
                .orElse(Collections.emptySet());
    }

    public String buildAbsoluteUrl(String serverPath, String path) {

        return new StringBuilder()
                .append(serverPath)
                .append(path)
                .toString();
    }

    public static Optional<Property> getPropertyByLocale(Node node, String name, String locale) {
        String propName = StringUtils.isEmpty(locale) ? name : name + "_" + locale;
        try {
            if (node.hasProperty(propName)) {
                return Optional.of(node.getProperty(propName));
            }
        } catch (RepositoryException e) {
            log.warn("Can't get property because {}", e.getMessage());
        }
        return Optional.empty();
    }
}
