package org.openxava.web.servlets;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import org.apache.commons.logging.*;
import org.openxava.util.jxls.*;

/**
 * @author Laurent Wibaux
 */
public class ReportXLSServlet extends HttpServlet {
	
	public static final String SESSION_XLS_REPORT = "org.openxava.report.xls";
	
	private static final long serialVersionUID = 7971363697569814431L;

	private static Log log = LogFactory.getLog(ReportXLSServlet.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JxlsWorkbook xlsReport = (JxlsWorkbook)request.getSession().getAttribute(SESSION_XLS_REPORT);
		request.getSession().removeAttribute(SESSION_XLS_REPORT); 
		try {
			xlsReport.write(response);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ServletException(ex.getMessage());
		}		
	}
}
