package org.openxava.actions;

import org.openxava.util.jxls.*;
import org.openxava.web.servlets.*;


/**
 * 
 * @author Laurent Wibaux
 */
public class ReportXLSAction extends ViewBaseAction 
implements IForwardAction, JxlsConstants {
	
	private String forwardURI = null;
	
	public void execute() throws Exception {
		JxlsWorkbook workbook = new JxlsWorkbook("Test");
		JxlsSheet sheet = workbook.addSheet("Test");
		JxlsStyle intS = workbook.addStyle("INT");
		JxlsStyle sumS = workbook.addStyle("INT").setBold().setBorder(TOP, BORDER_THIN);
		for (int row=1; row<10; row++) sheet.setValue(1, row, row*2, intS);
		sheet.setFormula(1, 11, "=SUM(R1C1:R10C1)", sumS);
		getRequest().getSession().setAttribute(ReportXLSServlet.SESSION_XLS_REPORT, workbook);
		setForwardURI("/xava/report.xls?time=" + System.currentTimeMillis());
	}
	
	public String getForwardURI() {		
		return forwardURI;
	}

	public boolean inNewWindow() {
		if (forwardURI == null) return false;
		return true;
	}

	public void setForwardURI(String forwardURI) {
		this.forwardURI = forwardURI;
	}	
}

