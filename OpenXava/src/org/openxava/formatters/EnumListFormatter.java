package org.openxava.formatters;

import javax.servlet.http.*;

import org.apache.commons.beanutils.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * tmp
 * @author Javier Paniza
 */
public class EnumListFormatter implements IMetaPropertyFormatter {


	public String format(HttpServletRequest request, MetaProperty metaProperty, Object object) throws Exception {
		System.out.println("[EnumListFormatter.parse] string.3=" + object); // tmp
		/*
		// tmp return object==null?"":object.toString();
		try {
			String icon = BeanUtils.getProperty(object, "icon");
			if (!Is.emptyString(icon)) {
				return "<i class='mdi mdi-" + icon + "'></i> " + metaProperty.format(object, Locales.getCurrent());
			}
		}
		catch (Exception ex) {
			ex.printStackTrace(); // tmp Quitar
		}
		*/
		// tmp ini
		String label = metaProperty.format(object, Locales.getCurrent());
		if (object instanceof IIconEnum) {
			return "<i class='mdi mdi-" + ((IIconEnum) object).getIcon() + 
				"' title='" + label + "'></i>" +
				"<span style='display: none'>" + label + "</span>";
		}
		return label;
		// tmp fin
		// tmp return metaProperty.format(object, Locales.getCurrent());
	}

	public Object parse(HttpServletRequest request, MetaProperty metaProperty, String string) throws Exception {
		return null;
	}


}
