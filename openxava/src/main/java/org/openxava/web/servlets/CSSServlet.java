package org.openxava.web.servlets;

import java.io.*;
import java.nio.charset.*;

import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

import org.apache.commons.io.*;
import org.apache.commons.logging.*;
import org.openxava.controller.*;
import org.openxava.util.*;

/**
 * @author Chungyen Tsai
 */

@WebServlet("/xava/style/*")
public class CSSServlet extends HttpServlet {
	
	private static Log log = LogFactory.getLog(CSSServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			long ini = System.currentTimeMillis(); // tmr
			if (request.getRequestURI().endsWith(".css")) {
				InputStream inputStream = getCSSAsStream(request.getPathInfo(), request.getServletPath());
				StringWriter writer = new StringWriter();
				IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
				String data = writer.toString().replaceAll("@import (['\"].*)\\.css", "@import $1.css?ox=" + ModuleManager.getVersion());
				
				response.getWriter().append(data);
			}
			long cuesta = System.currentTimeMillis() - ini; // tmr
			System.out.println("[CSSServlet.doGet] " + request.getRequestURI() + "=" + cuesta); // tmp
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServletException(XavaResources.getString("attachments_css_servlet_error", request.getServletPath() + request.getPathInfo()));
		}
	}
	
	private InputStream getCSSAsStream(String resourceName, String prefix) {
		// TMR ME QUEDÉ POR AQUÍ
		// TMR NO HACE CACHÉ
		/* TMR NOTAS:
		 * - rose no pink
		   - } catch (Exception e) { } : No
		   - materialdesignicons.css.map No se devolvía
		   - En i18n attachments_css_servlet_error bajo a versión 7.0
		 * 
		 */
		InputStream stream = null;
		try {
			// tmr if (getClass().getClassLoader().getResourceAsStream("META-INF/resources/" + prefix + resourceName) != null) return stream = getClass().getClassLoader().getResourceAsStream("META-INF/resources/" + prefix + resourceName);
			// tmr ini
			stream = getClass().getClassLoader().getResourceAsStream("META-INF/resources/" + prefix + resourceName);
			if (stream != null) return stream;
			// tmr fin
			
			FileInputStream file = new FileInputStream(getServletContext().getRealPath("/") + prefix + resourceName);
			// tmr if (file != null) return stream = file;
			if (file != null) return file;
		} catch (Exception e) { }
		return stream;
	}

}
