package org.openxava.actions;

import org.apache.commons.logging.*;
import org.openxava.hibernate.*;
import org.openxava.jpa.*;
import org.openxava.util.*;
import org.openxava.view.*;

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
		getRequest().getSession().setAttribute("xava_selectedRowsReportTab", getTab().getSelected()); // We use this most times for peformance
		getRequest().getSession().setAttribute("xava_selectedKeysReportTab", getTab().getSelectedKeys()); // We use this for cases when selected are out of loaded rows
		
		String hibernateDefaultSchema = getHibernateDefaultSchema();
		if (!Is.emptyString(hibernateDefaultSchema)) {
			getRequest().getSession().setAttribute("xava_hibernateDefaultSchemaTab", hibernateDefaultSchema);
		}
		if (!Is.emptyString(XPersistence.getDefaultSchema())) {
			getRequest().getSession().setAttribute("xava_jpaDefaultSchemaTab", XPersistence.getDefaultSchema());
		}
		getRequest().getSession().setAttribute("xava_user", Users.getCurrent());
	}
	
	private String getHibernateDefaultSchema() {  
		try {
			return XHibernate.getDefaultSchema();
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("hibernate_default_schema_warning", "__UNKNOWN__")); 
			return "__UNKNOWN__"; // Not null in order to avoid security holes
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
