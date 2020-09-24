package vn.ekino.certificate.util;

import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.objectfactory.Components;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import vn.ekino.certificate.config.mapper.NodeMapping;
import vn.ekino.certificate.repository.DamRepository;
import vn.ekino.certificate.repository.NodeRepository;
import vn.ekino.certificate.repository.NotSupportNodeRepository;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.jcr.PropertyType.*;

@UtilityClass
@Slf4j
public class MapperUtils {

    /**
     * create and map all property of source to target object
     * all property suppose to be same
     * @param source
     * @param target
     * @param <T>
     * @return
     */
    public static <T> Optional<T> nodeToObject(Node source, Class<T> target) {
        // find default constructor for target class
        Optional<Constructor<?>> constructor = Stream.of(target.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == 0)
                .findFirst();
        if (!constructor.isPresent()) {
            return Optional.empty();
        }

        try {
            T targetObj = (T) constructor.get().newInstance();
            PropertyIterator propertyIterator = source.getProperties();
            while (propertyIterator.hasNext()) {
                Property property = propertyIterator.nextProperty();
//                boolean i18n = getI18n(property.getName(), target);

                Object value = null;
                switch (property.getType()) {
                    case STRING:
                        if (property.isMultiple()) {
                            value = PropertyUtil.getValuesStringList(property.getValues());
                        } else {
                            value = property.getString();
                        }
                        break;
                    case BOOLEAN:
                        value = property.getBoolean();
                        break;
                    case DATE:
                        value = TimeUtils.toLocalDateTime(property.getDate());
                        break;
                    case DECIMAL:
                        value = property.getDecimal();
                        break;
                    case DOUBLE:
                        value = property.getDouble();
                        break;
                    case LONG:
                        value = property.getLong();
                        break;
                    case BINARY:
                        // for binary we will parse to string with base64 encode
                        StringWriter writer = new StringWriter();
                        IOUtils.copy(property.getBinary().getStream(), writer, "UTF-8");
                        value = Base64.getUrlEncoder().encodeToString(writer.toString().getBytes());
                        break;
                    default:
                        break;
                }

                Optional<Field> field = getTargetField(property.getName(), target);
                if (!field.isPresent()) {
                    continue;
                }

                Class<NodeRepository> reference = getNodeMappingAnnotation(field.get())
                        .map(NodeMapping::reference)
                        .orElse((Class) NotSupportNodeRepository.class);

                Object refValue = null;
                if (!reference.equals(NotSupportNodeRepository.class)) {
                    NodeRepository nodeRepository = Components.getComponent(reference);
                    boolean damWorkspace = DamRepository.class.getSimpleName().equals(nodeRepository.getClass().getSimpleName());
                    DamRepository damRepository = damWorkspace ? (DamRepository) nodeRepository : null;

                    if (field.get().getType().equals(List.class)) {
                        // list of Nodes
                        List<Node> listNode = damWorkspace ? damRepository.findByIdIn((List) value, property.getName()) : nodeRepository.findByIdIn((List) value);
                        refValue = listNode.stream()
                                .map(node -> nodeToObject(node, getGenericTypeClass(field.get().getGenericType())) )
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .collect(Collectors.toList());

                    } else {
                        // single Node
                        var singleObject = damWorkspace ? damRepository.findById((String) value, property.getName()) : nodeRepository.findById((String) value);
                        refValue = singleObject.isPresent() ? ((Optional) nodeToObject(singleObject.get(),
                                field.get().getType())).get() :
                                field.get().getType().newInstance();
                    }
                } else {
                    refValue = value;
                }

                Optional<Method> method = getSetterMethod(field.get().getName(), target, refValue.getClass());
                if (method.isPresent() && refValue != null) {
                    method.get().invoke(targetObj, refValue);
                }
            }

            return Optional.of(targetObj);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.warn("can't create target class {} : {}", target.getName(), e.getMessage());
        } catch (RepositoryException e) {
            log.warn("can't get properties from node: {}", e.getMessage());
        } catch (IOException e) {
            log.warn("can't read from stream: {}", e.getMessage());
        }
        return Optional.empty();
    }

    private static Class<?> getGenericTypeClass(Type type) {
        if (type instanceof ParameterizedType) {
            for (Type actualType : ((ParameterizedType) type).getActualTypeArguments()) {

                return (Class<?>) actualType;
            }
        }
        return null;
    }

    /**
     * get setter method by its field name
     * @param fieldName
     * @param target
     * @param setterType
     * @param <T>
     * @return
     */
    private static <T> Optional<Method> getSetterMethod(String fieldName, Class<T> target, Class<?> setterType) {
        String setter = String.format("set%C%s", fieldName.charAt(0), fieldName.substring(1));
        try {
            if (setterType.equals(ArrayList.class)) {
                return Optional.of(target.getMethod(setter, List.class));
            }
            return Optional.of(target.getMethod(setter, setterType));
        } catch (NoSuchMethodException e) {
            log.warn("can't get method {} from target class {} : {}", setter, target.getName(), e.getMessage());
        }

        return Optional.empty();
    }

    private static <T> Optional<Field> getTargetField(String propertyName, Class<T> target) {
        return Stream.of(FieldUtils.getAllFields(target))
                .filter(f -> propertyName.equals(f.getName()) ||
                        propertyName.equals(
                                getNodeMappingAnnotation(f).map(NodeMapping::propertyName).orElse("")))
                .findFirst();
    }

    private static Optional<NodeMapping> getNodeMappingAnnotation(Field field) {
        List<Annotation> annotations = Stream.of(field.getDeclaredAnnotations())
                .filter(annotation -> annotation.annotationType().equals(NodeMapping.class))
                .collect(Collectors.toList());
        return annotations.isEmpty() ?
                Optional.empty() : Optional.of((NodeMapping) annotations.get(0));
    }

}
