package org.openxava.formatters;

import javax.servlet.http.*;




/**
 * Simply by pass the values from property to html and viceversa. <p>
 * 
 * It's useful if you have a property of type String [] and you editor
 * may work with its values directly.<br> 
 * 
 * @author Javier Paniza
 */

public class MultipleValuesByPassFormatter implements IMultipleValuesFormatter {

	
	
	public String [] format(HttpServletRequest request, Object object) throws Exception {	
		return (String []) object;
	}

	public Object parse(HttpServletRequest request, String [] strings) throws Exception {		
		return strings;
	}

}
