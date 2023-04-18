package org.openxava.test.formatters;

import javax.servlet.http.*;

import org.openxava.formatters.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class CarColorFormatter implements IFormatter  {

	public String format(HttpServletRequest request, Object object) throws Exception {
		Car.Color color = (Car.Color) object;
		if (color == null || color == Car.Color.UNSPECIFIED) return "";
		return "<nobr><font color='" + color.name() + "'><i class='mdi mdi-square ox-color-inherit'></i></font><span class='ox-vertical-align-sub'> : " + Strings.firstUpper(color.name().toLowerCase()) + "</span></nobr>"; 
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		return null;
	}

}
