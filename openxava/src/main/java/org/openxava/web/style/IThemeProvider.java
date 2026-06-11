package org.openxava.web.style; 

import jakarta.servlet.http.*;

/**
 * @since 6.4
 * @author Javier Paniza
 */
public interface IThemeProvider {
	
	String getCSS(HttpServletRequest request);

}
