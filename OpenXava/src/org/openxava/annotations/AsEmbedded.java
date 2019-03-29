package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * Makes that the behavior in the view for a reference (or collection) to entity
 * will be as in the case of a embedded object (or collection to entities with
 * CascadeType.REMOVE). <p>
 * 
 * <h4>Applied to references</h4>
 * By default in the case of a reference to an embedded object the user can create 
 * and edit its data, while in the case of a reference to an entity
 * the user can only to choose an existing entity. If you put <code>@{@link AsEmbedded}</code>
 * then the user interface for references to entities behaves as a in 
 * the embedded case, allowing to the user to create a new object and editing 
 * its data directly. It has no effect in case of a reference to embedded object.<br> 
 * <b>Warning!</b> If you remove an entity its referenced entities are not removed, 
 * even if they are displayed using <code>@{@link AsEmbedded}</code>.<br>
 * Example:
 * <pre>
 * &nbsp;@ManyToOne 
 * &nbsp;@AsEmbedded
 * &nbsp;private Seller seller;
 * </pre>
 * 
 * <h4>Applied to @OneToMany/@ManyToMany collections</h4> 
 * By default the collections with CascadeType.REMOVE allow the users to create 
 * and to edit elements, while the other collections allows only to choose existing 
 * entities to add to (or remove from) the collection. If you put <code>@{@link AsEmbedded}</code>
 * then the collection behaves always as a collection with CascadeType.REMOVE, 
 * allowing to the user to add objects and editing them directly.<br>
 * <b>Warning!</b> If you remove an entity from an <code>@AsEmbedded</code> collection 
 * (so with no CascadeType.REMOVE), the relationship with the parent is removed but the entity 
 * itself still exists, unless you use <code>orphanRemoval=true</code>.<br> 
 * <b><i>Note:</i></b> JPA 1.0 does not support collections of embedded objects, therefore we
 * assume a collection of entities with CascadeType.REMOVE as a collection
 * of <i>embedded objects</i>.<br>
 * Example:
 * <pre>
 * &nbsp;@AsEmbedded
 * &nbsp;@OneToMany(mappedBy="seller")
 * &nbsp;private Collection<Customer> customers;
 * </pre>
 *  
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface AsEmbedded {
		
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
	
}
