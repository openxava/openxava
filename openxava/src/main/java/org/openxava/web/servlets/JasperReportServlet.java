package org.openxava.web.servlets;

import java.io.*;
import java.util.*;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.reports.DynamicListReportBuilder;
import org.openxava.tab.*;
import org.openxava.util.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.*;

/**
 * @author Javier Paniza
 */

@WebServlet(name="jasperReport", urlPatterns = "/xava/jasperReport")
public class JasperReportServlet extends HttpServlet {

	private static Log log = LogFactory.getLog(JasperReportServlet.class);
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		try {
			String language = request.getParameter("language");
			if (language == null) language = Locales.getCurrent().getDisplayLanguage();
			Locale locale = new Locale(language, "");
			String scolumnCountLimit = request.getParameter("columnCountLimit");
			Integer columnCountLimit = scolumnCountLimit == null ? null : Integer.parseInt(scolumnCountLimit);
			int[] widths = parseWidths(request.getParameter("widths"), columnCountLimit);
			
			Tab tab = (Tab) request.getSession().getAttribute("xava_reportTab");
			JasperDesign design = new DynamicListReportBuilder(tab, widths, columnCountLimit, locale, request).getJasperDesign();
			JasperCompileManager.compileReportToStream(design, response.getOutputStream()); 
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ServletException(XavaResources.getString("jasper_error"));
		}		
	}
	
	private int[] parseWidths(String widths, Integer columnCountLimit) {
		String[] tokens = widths.split("[\\[\\], ]+");
		int size = columnCountLimit == null ? tokens.length - 1 : columnCountLimit.intValue();
		int[] result = new int[size];
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.parseInt(tokens[i + 1]);
		}
		return result;
	}
		
}
