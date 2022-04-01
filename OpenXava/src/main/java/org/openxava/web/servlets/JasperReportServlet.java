package org.openxava.web.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.sf.jasperreports.engine.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class JasperReportServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(JasperReportServlet.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		try {
			String language = request.getParameter("language");
			String columnCountLimit = request.getParameter("columnCountLimit");
			String widths = request.getParameter("widths"); 
			
			ServletContext application = request.getSession().getServletContext();		
									
			System.setProperty("jasper.reports.compile.class.path",					 
					application.getRealPath("/WEB-INF/lib/jasperreports.jar") +
					System.getProperty("path.separator") + 
					application.getRealPath("/WEB-INF/classes/")
					);											
			JasperCompileManager.compileReportToStream(getReportStream(request, response, language, columnCountLimit, widths), response.getOutputStream()); 
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ServletException(XavaResources.getString("jasper_error"));
		}		
	}
	
	private InputStream getReportStream(HttpServletRequest request, HttpServletResponse response, String language, String columnCountLimit, String widths) throws IOException, ServletException { 
		StringBuffer suri = new StringBuffer();
		suri.append("/xava/jasperReport");		
		suri.append(".jsp?language="); 
		suri.append(language);
		suri.append("&widths="); 
		suri.append(widths);			
		if (columnCountLimit != null) {
			suri.append("&columnCountLimit="); 
			suri.append(columnCountLimit);			
		}
		return Servlets.getURIAsStream(request, response, suri.toString(), XSystem.getEncoding());
	}
		
}
