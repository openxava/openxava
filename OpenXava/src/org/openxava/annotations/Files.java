package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * tmr Redoc
 * Associates an action to a property or reference in the view. <p>
 * 
 * Applies to properties and references.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@Action("Deliveries.generateNumber")
 * &nbsp;private int number;
 * </pre>
 * The actions are displayed as a link or an image beside the property.<br>
 * By default the action link is present only when the property is editable, 
 * but if the property is read only or calculated then it is always present.<br>
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Files {
	
}
