package org.openxava.annotations;

import java.lang.annotation.*;
import org.hibernate.annotations.IdGeneratorType;

/**
 * Generates a 32-character hexadecimal UUID string without hyphens.
 * <p>
 * This is the standard identifier generation strategy for OpenXava's <code>Identifiable</code> base class.
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
