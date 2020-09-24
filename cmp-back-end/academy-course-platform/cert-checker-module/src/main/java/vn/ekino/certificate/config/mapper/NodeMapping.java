package vn.ekino.certificate.config.mapper;

import vn.ekino.certificate.repository.NodeRepository;
import vn.ekino.certificate.repository.NotSupportNodeRepository;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Configures the mapping of the node property name
 */
@Target({ FIELD })
@Retention(RUNTIME)
public @interface NodeMapping {

    /**
     * The source to use for this mapping. This can be node property name
     * @return
     */
    String propertyName() default "";

    /**
     * reference is the repository class
     * it will call findById method to get reference Node
     * by default, it will be NotSupportNodeRepository
     * @return
     */
    Class<? extends NodeRepository> reference() default NotSupportNodeRepository.class;
}
