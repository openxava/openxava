package org.openxava.web;

import org.openxava.model.meta.*;
import org.openxava.view.*;

/**
 * Utilities used from JSP and taglibs for editors. 
 * 
 * @since 7.0.5
 * @author Javier Paniza 
 */

public class EditorsJS {
	
	public static String calculateScript(String application, String module, View rootView, String sumProperty) {  
		String calculatedProperty = rootView.getDependentCalculationPropertyNameFor(sumProperty);
		String calculatedPropertyKey = org.openxava.web.Ids.decorate(application, module, calculatedProperty);
		MetaProperty calculatedMetaProperty = rootView.getMetaProperty(calculatedProperty);
		return " onchange=\"openxava.calculate(" +
			"'" + application + "'," +
			"'" + module + "'," +
			"'" + calculatedPropertyKey + "'," +
			"'" + calculatedMetaProperty.getScale() + "'" + 
			")\"";
	}

}
