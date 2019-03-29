package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Allows to define a subcontroller in a collection.<p> 
 * 
 * Applies to @OneToMany/@ManyToMany collections. <p>
 * 
 * The collection will be display a 'menu' with the controller actions.<br>
 * Example:
 * <pre>
 * &nbsp;@OneToMany(mappedBy="team", cascade=CascadeType.ALL)
 * &nbsp;@ListSubcontroller("MyControllerName")
 * &nbsp;private Collection<TeamMember> members;		
 * </pre>
 *
 * @since 5.7
 * @author Ana Andres
 */

@Repeatable(ListSubcontrollers.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface ListSubcontroller {

	/**
	 * List of comma separated view names where this annotation applies. <p>
	 * 
	 * Exclusive with notForViews.<br>
	 * If both forViews and notForViews are omitted then this annotation
	 * apply to all views.<br>
	 * You can use the string "DEFAULT" for referencing to the default
	 * view (the view with no name).
	 */		
	String forViews() default "";
	
	/**
	 * List of comma separated view names where this annotation does not apply. <p>
	 * 
	 * Exclusive with forViews.<br>
	 * If both forViews and notForViews are omitted then this annotation
	 * apply to all views.<br>
	 * You can use the string "DEFAULT" for referencing to the default
	 * view (the view with no name).
	 */ 		
	String notForViews() default "";

	/**
	 * You have to write the controller name that has all the actions that you want displaying <p>
	 * 
	 * This controller must be registered in controllers.xml.
	 */	
	String value();
	
}
