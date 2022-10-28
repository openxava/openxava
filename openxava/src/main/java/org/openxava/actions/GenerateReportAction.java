package org.openxava.actions;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */

public class GenerateReportAction extends TabBaseAction implements IForwardAction { 

	private static Log log = LogFactory.getLog(GenerateReportAction.class);
	private String type;

	public void execute() throws Exception {
		if (!("pdf".equals(getType()) || "csv".equals(getType()) || "xls".equals(getType()))) {
            throw new XavaException("report_type_not_supported", getType(), "pdf, csv, xls");
        }
		getRequest().getSession().setAttribute("xava_reportTab", getTab());	
		
		if (!Is.emptyString(XPersistence.getDefaultSchema())) {
			getRequest().getSession().setAttribute("xava_jpaDefaultSchemaTab", XPersistence.getDefaultSchema());
		}
	}
	
	public boolean inNewWindow() {
		return true; 
	}

	public String getForwardURI() {
		if (isAndroid() && "pdf".equals(getType())) {
			return "/xava/js/pdfjs/web/viewer.html?file=/" + getRequest().getParameter("application") + "/xava/list.pdf";
		}
		else {
			return "/xava/list." + getType() + 
				"?application=" + getRequest().getParameter("application") +
				"&module=" + getRequest().getParameter("module") +
				"&time=" + System.currentTimeMillis();
		}
	}
	
	private boolean isAndroid() { 
		String browser = getRequest().getHeader("user-agent");
		return browser != null && browser.contains("Android");
	}

	public String getType() {
		return type;
	}

	public void setType(String string) {
		type = string;
	}

}
