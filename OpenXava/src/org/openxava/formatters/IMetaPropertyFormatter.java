package org.openxava.formatters;

import javax.servlet.http.*;

import org.openxava.model.meta.*;

/**
 * For convert to String (used in HTML page) to Object (used in java side),
 * and vice versa using MetaProperty info. <p>
 * 
 * It's like IFormatter but the format and parse method have a MetaProperty argument, so you can use label,
 * size, scale and other MetaProperty data to do the formatting.
 * 
 * @since 5.9
 * @author Javier Paniza
 */

public interface IMetaPropertyFormatter { 
	
	/**
	 * From a object return a <code>String</code> to render in HTML.   
	 */
	public String format(HttpServletRequest request, MetaProperty metaProperty, Object object) throws Exception;
	/**
	 * From a <code>String</code> obtained from a HTTP request return a java object.
	 */
	public Object parse(HttpServletRequest request, MetaProperty metaProperty, String string) throws Exception;

}
