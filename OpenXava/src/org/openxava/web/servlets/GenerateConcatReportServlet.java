package org.openxava.web.servlets;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.export.oasis.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * 
 * @author Jeromy Altuna
 */
@SuppressWarnings("serial")
public class GenerateConcatReportServlet extends HttpServlet {
	
	private static Log log = LogFactory.getLog(GenerateConcatReportServlet.class);

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String filename = (String) request.getSession().getAttribute("xava.report.filename");
		String format = (String) request.getSession().getAttribute("xava.report.format");
		request.getSession().removeAttribute("xava.report.filename");
		request.getSession().removeAttribute("xava.report.format");

		JasperPrint[] jprints = (JasperPrint[]) request.getSession().getAttribute("xava.report.jprints");
		request.getSession().removeAttribute("xava.report.jprints");

		try {
			JRExporter exporter = null;
			if (format.equals(JasperReportBaseAction.EXCEL)) {
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition", "inline; filename=\"" + filename + ".xls\"");
				exporter = new JRXlsExporter();
			} else if (format.equalsIgnoreCase(JasperReportBaseAction.RTF)) {
				response.setContentType("application/rtf");
				response.setHeader("Content-Disposition", "inline; filename=\""	+ filename + ".rtf\"");
				exporter = new JRRtfExporter();
			} else if (format.equalsIgnoreCase(JasperReportBaseAction.ODT)) {
				response.setContentType("application/vnd.oasis.opendocument.text");
				response.setHeader("Content-Disposition", "inline; filename=\"" + filename + ".odt\"");
				exporter = new JROdtExporter();
			} else {
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "inline; filename=\""	+ filename + ".pdf\"");
				exporter = new JRPdfExporter();
			}
			exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, Arrays.asList(jprints));
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, response.getOutputStream());
			exporter.exportReport();

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ServletException(XavaResources.getString("report_error"));
		}
	}
}
