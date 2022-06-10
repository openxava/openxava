package org.openxava.web.style; 

import javax.servlet.http.*;

/**
 * @since 6.4
 * @author Javier Paniza
 */
public interface IThemeProvider {
	
	String getCSS(HttpServletRequest request);

}
