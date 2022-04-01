package org.openxava.formatters;

import java.math.*;
import java.text.*;

import javax.servlet.http.*;

import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * For MONEY and DINERO stereotypes. <p>
 * 
 * @author Javier Paniza
 */

public class MoneyFormatter implements IMetaPropertyFormatter {

	public String format(HttpServletRequest request, MetaProperty metaProperty, Object object) throws Exception {
		if (object == null) return "";
		return getFormat(metaProperty).format(object);
	}

	public Object parse(HttpServletRequest request, MetaProperty metaProperty, String string) throws Exception {
		if (Is.emptyString(string)) return null; 
		string = Strings.change(string, " ", ""); // In order to work with Polish		
		return new BigDecimal(getFormat(metaProperty).parse(string).toString()).setScale(2);
	}
	
	private NumberFormat getFormat(MetaProperty metaProperty) {
		int decimals = metaProperty.getScale() > 0?metaProperty.getScale():2;
		NumberFormat f = DecimalFormat.getNumberInstance(Locales.getCurrent());
		f.setMinimumFractionDigits(decimals);
		f.setMaximumFractionDigits(decimals);
		return f;
	}

}
