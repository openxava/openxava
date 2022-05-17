package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A search key property or reference is used by the user to search. <p>
 * 
 * Applies to properties and references.<p>
 * 
 * If the @{@link Id} property is @{@link Hidden} you can use 
 * <code>SearchKey</code> to indicate what property or reference will be used
 * for searching.<br>
 * By default, OpenXava allows to the user to search objects (in references
 * for example) by the @Id property, if the @Id property is hidden then
 * the first property of the view is used. In some case you want to 
 * choose what properties will be use for searching. @SearchKey is for that.
 *  
 * Example:
 * <pre>
 * &nbsp;@Id &nbsp;@GeneratedValue(generator="system-uuid") &nbsp;@Hidden 
 * &nbsp;@GenericGenerator(name="system-uuid", strategy = "uuid")
 * &nbsp;private String oid;
 *
 * &nbsp;@SearchKey
 * &nbsp;@Column(length=4) &nbsp;@DefaultValueCalculator(CurrentYearCalculator.class)
 * &nbsp;private int year;
	
 * &nbsp;@SearchKey
 * &nbsp;@Column(length=6)
 * &nbsp;private int number;
 * </pre>
 * In this case <code>oid</code> is a hidden id, and the user will be use
 * <code>year</code> and <code>number</code> for searching the objects in 
 * references. 
 * The properties <code>year</code> and <code>number</code> are search keys.
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface SearchKey {
	
}
