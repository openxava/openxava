package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * View of the referenced object used to create a new one from a reference or collection. <p>
 * 
 * Applies to references and collections. <p>
 * 
 * If you omit this annotation, then the default view of the referenced object 
 * is used when creating a new object from the reference. With this annotation 
 * you can indicate that it uses another view.<br>
 * Example for a reference:
 * <pre>
 * &nbsp;@ManyToOne 
 * &nbsp;@NewView("SimpleCreation")
 * &nbsp;private Seller seller;
 * </pre>
 * 
 * For collections, this annotation overrides the view specified by 
 * {@link CollectionView} when creating a new element. If you use 
 * <code>@CollectionView("Simple")</code> it's equivalent to using both 
 * <code>@NewView("Simple")</code> and <code>@EditView("Simple")</code>. 
 * You can use <code>@NewView</code> to specify a different view just for creation.<br>
 * Example for a collection:
 * <pre>
 * &nbsp;@OneToMany(mappedBy="invoice")
 * &nbsp;@CollectionView("Simple")
 * &nbsp;@NewView("SimpleCreation") // Overrides @CollectionView for creation
 * &nbsp;private Collection&lt;InvoiceDetail&gt; details;
 * </pre>
 * 
 * @author Javier Paniza
 * @since 7.7
 */

@Repeatable(NewViews.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface NewView {
	
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
	 * Name of a view present in the referenced object to use for creation. 
	 */
	String value();
	
}
