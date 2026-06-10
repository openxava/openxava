package org.openxava.annotations;

import java.lang.annotation.*;
import org.hibernate.annotations.IdGeneratorType;

/**
 * Generates a 32-character hexadecimal UUID string without hyphens.
 * <p>
 * This is the standard identifier generation strategy for OpenXava's <code>Identifiable</code> base class.
 * </p>
 * <p>
 * <b>Migration from older versions:</b>
 * </p>
 * <p>
 * In OpenXava 7 with Hibernate 5 you could use:
 * </p>
 * <pre>
 * &#64;Id &#64;GeneratedValue(generator="system-uuid") &#64;Hidden 
 * &#64;GenericGenerator(name="system-uuid", strategy = "uuid")
 * &#64;Column(length=32)
 * private String id;
 * </pre>
 * <p>
 * In OpenXava 8 with Hibernate 7 you can use:
 * </p>
 * <pre>
 * &#64;Id &#64;Hidden 
 * &#64;UUID32
 * &#64;Column(length=32)
 * private String id;
 * </pre>
 * <p>
 * This allows you to continue using a 32-character column without deprecated warnings.
 * </p>
 *
 * @since 8.0
 * @author Javier Paniza
 */
@IdGeneratorType(org.openxava.jpa.impl.UUID32Generator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface UUID32 {
}
