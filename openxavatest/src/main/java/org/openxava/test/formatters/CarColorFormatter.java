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
		// tmr return "<i class='mdi mdi-square' style='color: " + color.name() + "'></i><span style=\"vertical-align: bottom\">: " + Strings.firstUpper(color.name().toLowerCase()) + "</span>";
		// tmr ¿En migración?
		// TMR ME QUEDÉ POR AQUÍ: VOLVER A PROBARLO CON <font color=""> PORQUE SI FUNCIONA EN OTROS SITIOS (colorEdito.jsp)
		// TMR   SI FUNCIONA FONT QUITAR LOS ox-color- DE base.css
		// TMR 	 SEGUIR BUSCANDO style= EN openxavatest 
		return "<nobr><i class='mdi mdi-square ox-color-" + color.name().toLowerCase() + "'></i><span class='ox-vertical-align-sub'> :" + Strings.firstUpper(color.name().toLowerCase()) + "</span></nobr>";
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {
		return null;
	}

}
