package org.openxava.web.style; 

import javax.servlet.http.*;

/**
 * tmp 
 * @author Javier Paniza
 */
public interface IThemeProvider {
	
	String getCSS(HttpServletRequest request);

}
