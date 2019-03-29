/**
 * 
 */
package org.openxava.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Callback executed before creating an object.
 * This callback occurs within the transaction scope.
 * If any exception is thrown, the whole transaction is roll backed.
 * @author Federico Alcantara
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PreDelete {

}
