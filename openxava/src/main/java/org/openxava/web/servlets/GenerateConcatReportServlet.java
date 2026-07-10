package org.openxava.web.servlets;

import java.io.*;
import java.util.*;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.util.*;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.*;
import net.sf.jasperreports.engine.export.oasis.*;
import net.sf.jasperreports.export.*;
import net.sf.jasperreports.pdf.*;
import net.sf.jasperreports.poi.export.*;

/**
 * 
 * @author Jeromy Altuna
 */
@WebServlet("/xava/concatReport.pdf")
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
			if (format.equals(JasperReportBaseAction.EXCEL)) {
				response.setContentType("application/vnd.ms-excel");
				response.setHeader("Content-Disposition", "inline; filename=\"" + filename + ".xls\"");
				JRXlsExporter exporter = new JRXlsExporter();
				exporter.setExporterInput(SimpleExporterInput.getInstance(Arrays.asList(jprints)));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
				exporter.exportReport();
			} else if (format.equalsIgnoreCase(JasperReportBaseAction.RTF)) {
				response.setContentType("application/rtf");
				response.setHeader("Content-Disposition", "inline; filename=\""	+ filename + ".rtf\"");
				JRRtfExporter exporter = new JRRtfExporter();
				exporter.setExporterInput(SimpleExporterInput.getInstance(Arrays.asList(jprints)));
				exporter.setExporterOutput(new SimpleWriterExporterOutput(response.getWriter()));
				exporter.exportReport();
			} else if (format.equalsIgnoreCase(JasperReportBaseAction.ODT)) {
				response.setContentType("application/vnd.oasis.opendocument.text");
				response.setHeader("Content-Disposition", "inline; filename=\"" + filename + ".odt\"");
				JROdtExporter exporter = new JROdtExporter();
				exporter.setExporterInput(SimpleExporterInput.getInstance(Arrays.asList(jprints)));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
				exporter.exportReport();
			} else {
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "inline; filename=\""	+ filename + ".pdf\"");
				JRPdfExporter exporter = new JRPdfExporter();
				exporter.setExporterInput(SimpleExporterInput.getInstance(Arrays.asList(jprints)));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
				exporter.exportReport();
			}

		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new ServletException(XavaResources.getString("report_error"));
		}
	}
}
