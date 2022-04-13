package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * The eXtended version of <code>@OrderBy</code>. <p>
 * 
 * Applies to collections.<p>
 * 
 * The <code>@OrderBy</code> of JPA does not allow to use qualified properties 
 * (properties of references).<br>
 * OpenXava has its own version of <code>@OrderBy</code>, this <code>@XOrderBy</code>
 * to allow it.
 * Example:
 * <pre>
 * &nbsp;@OneToMany (mappedBy="invoice", cascade=CascadeType.REMOVE)
 * &nbsp;@ListProperties("product.description, quantity, unitPrice, amount")
 * &nbsp;@XOrderBy("product.description desc") 
 * &nbsp;private Collection&lt;InvoiceDetail&gt; details;
 * </pre>
 * You can note as <code>product.description</code> can be used for ordering the collection.<br>
 * In order to use a qualified property, it must be included in the <code>@{@link ListProperties}</code>,
 * as in this case with <code>product.description</code>.<p>
 * 
 * The order in <code>@XOrderBy</code> has only effect at visual level, when 
 * you access programmatically to the collection, the collection is ordered as 
 * indicated by the JPA <code>@OrderBy</code> or by its default order.<br> 
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface XOrderBy {

	/**
	 * The order using the syntax of JPA <code>@OrderBy</code> but allowing 
	 * qualified properties with any level of indirection.
	 */
	String value();
	
}
