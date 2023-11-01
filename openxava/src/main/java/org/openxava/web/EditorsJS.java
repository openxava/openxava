package org.openxava.web;

import org.openxava.model.meta.*;
import org.openxava.view.*;

/**
 * Utilities used from JSP and taglibs for editors. 
 * 
 * @since 7.0.5
 * @author Javier Paniza 
 */

public class EditorsJS { // tmr Renombrar como Editors

	/* tmr
	public static String calculateScript(String application, String module, View rootView, String sumProperty) { // tmr Quitar
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
	*/
	
	public static String onChangeCalculateDataAttributes(String application, String module, View rootView, String changedProperty) { // tmr 
		String calculatedProperty = rootView.getDependentCalculationPropertyNameFor(changedProperty);
		String calculatedPropertyKey = org.openxava.web.Ids.decorate(application, module, calculatedProperty);
		MetaProperty calculatedMetaProperty = rootView.getMetaProperty(calculatedProperty);
		StringBuffer sb = new StringBuffer();
		sb.append("data-calculated-property='");
		sb.append(calculatedPropertyKey);
		sb.append("' data-scale='");
		sb.append(calculatedMetaProperty.getScale());
		sb.append("'");
		return sb.toString();
	}

}
