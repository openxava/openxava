package org.openxava.test.portlets;

import java.io.*;
import javax.portlet.*;
import org.openxava.controller.*;

/**
 * 
 * @author Laurent Wibaux 
 */

public class VersionPortlet extends GenericPortlet {
		
	public void doView(RenderRequest request, RenderResponse response) throws PortletException, IOException {
		response.setContentType("text/html");
		response.getWriter().write("<table border=\"0\">");
		writeVersion(response.getWriter(), "The version of OpenXava is", ModuleManager.getVersion());
		response.getWriter().write("</table>");
	}
	
	private void writeVersion(PrintWriter out, String unit, String version) {
		out.write("<tr>");
		out.write("<td style=\"padding: 2px 5px 2px 5px;\"><b>" + unit + "</b></td>");
		out.write("<td style=\"padding: 2px 5px 2px 5px;\">v" + version + "</td>");
		out.write("</tr>");
	}
	
}