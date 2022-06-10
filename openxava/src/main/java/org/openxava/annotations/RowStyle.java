package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * For indicating the row style for {@link Tab}s and collections . <p>
 * 
 * Example for Tab:
 * <pre>
 * &nbsp;@Tab(
 * &nbsp;&nbsp;&nbsp;rowStyles=@RowStyle(style="row-highlight", property="type", value="steady")
 * &nbsp;)
 * &nbsp;public class Customer {
 * &nbsp;...
 * </pre>
 * Example for Collection:
 * <pre>
 * &nbsp;@OneToMany(mappedBy="seller")
 * &nbsp;@ListProperties("number, name, remarks, relationWithSeller, seller.level.description, type") 
 * &nbsp;@RowStyle(style="row-highlight", property="type", value="steady") 
 * &nbsp;private Collection<Customer> customers;
 * </pre>
 * In this case you are saying that the object which property type has the 
 * value steady will use the style row-highlight. The style has to be defined in the
 * CSS stylesheet. The <i>row-highlight</i> style are already defined in OpenXava, but 
 * you can define more.<br>
 * You can note as property 'type' is also listed in ListProperties, that is
 * you must use properties displayed in User Interface.<br>
 * It doesn't work with @ElementCollection.<br>
 * 
 * @author Javier Paniza
 */

@Repeatable(RowStyles.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD }) 
public @interface RowStyle {
	
	/** 
	 * List of comma separated view names where this annotation applies. <p>
	 * 
	 * <code>forViews</code> has no effect when <code>@RowStyle</code> is used for Tabs.
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
	 * <code>notforViews</code> has no effect when <code>@RowStyle</code> is used for Tabs.
	 * 
	 * Exclusive with forViews.<br>
	 * If both forViews and notForViews are omitted then this annotation
	 * apply to all views.<br>
	 * You can use the string "DEFAULT" for referencing to the default
	 * view (the view with no name).
	 */ 	
	String notForViews() default "";
	
	
	/**
	 * The name of the style to apply. <p>
	 * 
	 * Must be a style defined in the CSS.
	 */
	String style();
	
	/**
	 * Property to evaluate. <p>
	 * 
	 * If value of this 'property' is the one indicate in 'value', 
	 * then the 'style' apply to this row.<br>  
	 * This property must be present in User Interface.
	 */
	String property();
	
	/**
	 * Value to compare with value property. <p>
	 * 
	 * If value of 'property' is the one indicate here, 
	 * then the 'style' apply to this row.  
	 */
	String value();
	
}
