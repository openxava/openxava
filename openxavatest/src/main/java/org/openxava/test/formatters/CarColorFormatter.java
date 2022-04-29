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
		return "<i class='mdi mdi-square' style='color: " + color.name() + "'></i><span style=\"vertical-align: bottom\">: " + Strings.firstUpper(color.name().toLowerCase()) + "</span>";
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		return null;
	}

}
