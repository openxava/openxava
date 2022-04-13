package org.openxava.annotations;

import java.lang.annotation.*;


/**
 * With <code>@Tree</code> you can instruct OpenXava to visualize collections 
 * as a tree instead of a list. <p> 
 * 
 * Applies to @OneToMany/@ManyToMany collections.
 * 
 * Example:
 * <pre>
 * &nbsp;@OneToMany(mappedBy="parentContainer", cascade = CascadeType.REMOVE)
 * &nbsp;@Editor("TreeView")
 * &nbsp;@ListProperties("description")
 * &nbsp;@OrderBy("path, treeOrder")
 * &nbsp;private Collection<TreeItem> treeItems;
 * </pre>
 * <br/>
 * The full syntax for @Tree is @Tree(forViews="", notForViews="", pathProperty="path", idProperties="", idSeparator=",", initialExpandedState=true, orderIncrement=2, pathSeparator="/").
 * <ol>
 * <li><b>forViews</b>. Indicates in which views the tree behavior is going to be applied.</li>
 * <li><b>notForViews</b>. Views which are excluded from the tree renderization.</li>
 * <li><b>pathProperty</b>. Indicates the property to be used as the path, by default points to "path". 
 * You can customize this property if the entity path reference is persisted in a property with another name.</li>
 * <li><b>idProperties</b>. The tree implementation needs a unique identification for each of the elements displayed. 
 * By default the tree implementation use the properties annotated with @Id, 
 * but you can define which properties the tree must use as the unique identifier. 
 * The defined properties are comma separated.</li>
 * <li><b>initialExpandedState</b>. If true the tree is rendered with all nodes expanded.</li>
 * <li><b>orderIncrement</b>. If you define a orderBy and the orderBy is an integer type, 
 * the Tree implementation uses this field to allow reordering of the tree elements, 
 * by default the increment value is 2 and is the minimum allowed. 
 * This value allow easy reordering of elements.</li>
 * <li><b>pathSeparator</b>. If you use a separator for your path different than the default "/". 
 * Then you can set this property to the value that you are using.</li>
 * @author Federico Alcantara
 *
 */

@Repeatable(Trees.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD})
public @interface Tree {
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
	 * Optional. Defaults to path.
	 * Property used for the path, must be a String type with 
	 * a size appropriate to the task at hand.
	 * @return property name
	 */
	String pathProperty() default "path";
	
	/**
	 * Optional.
	 * Comma separated list of properties used for identifying the tree node. By default
	 * the id of the entity is used. If more than one property is used, 
	 * their values will be nodeIdSeparator separated and enclosed in brackets.
	 * @return node property name.
	 */
	String idProperties() default ""; 
	
	/**
	 * Optional.
	 * String to be used to separate multiple Id elements. 
	 */
	String idSeparator() default ",";
			
	/**
	 * Optional.
	 * Indicates how to render the tree when the expandedPropertyName is
	 * not defined. It's default value is true. 
	 * @return expanded state.
	 */
	boolean initialExpandedState() default true;

	/**
	 * Optional.
	 * Defines the increment used for the keys when orderBy is used
	 * The default value is 2. The minimum is 2.
	 * @return order increment.
	 */
	int orderIncrement() default 2;
	
	/**
	 * Optional.
	 * Defines the separator for the path elements. Default value is / 
	 * @return path separator character.
	 */
	String pathSeparator() default "/";
	
}
