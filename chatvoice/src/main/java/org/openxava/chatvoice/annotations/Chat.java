package org.openxava.chatvoice.annotations;

import java.lang.annotation.*;

/**
 * Annotation to mark a property as a chat editor.
 * 
 * @author chatvoice
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Chat {
    
}
