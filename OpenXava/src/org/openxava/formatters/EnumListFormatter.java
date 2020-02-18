package org.openxava.formatters;

import javax.servlet.http.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.web.style.*;

/**
 * tmp
 * @author Javier Paniza
 */
public class EnumListFormatter implements IMetaPropertyFormatter {


	public String format(HttpServletRequest request, MetaProperty metaProperty, Object object) throws Exception {
		// tmp String label = metaProperty.format(object, Locales.getCurrent());
		if (object == null) return "";
		String label = object instanceof Number?
			metaProperty.getValidValueLabel(((Number) object).intValue()):
			metaProperty.getValidValueLabel(object);
		Style style = (Style) request.getAttribute("style");	
		if (object instanceof IIconEnum) {
			return "<span class='" + style.getIconInList() + "'><i class='mdi mdi-" + ((IIconEnum) object).getIcon() + 
				"' title='" + label + "'></i>" +
				"<span style='display:none'>" + label + "</span></span>";
		}
		return label;
	}

	public Object parse(HttpServletRequest request, MetaProperty metaProperty, String string) throws Exception {
		return null;
	}


}
