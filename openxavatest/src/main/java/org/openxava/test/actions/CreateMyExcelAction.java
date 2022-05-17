package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.util.jxls.*;
import org.openxava.web.servlets.*;

/**
 * 
 * @author Laurent Wibaux
 * @author Javier Paniza
 */
 
public class CreateMyExcelAction extends ViewBaseAction implements IForwardAction, JxlsConstants {                                                             
 
    private String forwardURI = null;
 
    public void execute() throws Exception {
        JxlsWorkbook workbook = createWorkbook(); 
        getRequest().getSession().setAttribute(ReportXLSServlet.SESSION_XLS_REPORT, workbook);   
        setForwardURI("/xava/report.xls?time=" + System.currentTimeMillis());                      
    }
 
    private JxlsWorkbook createWorkbook() throws Exception {
        JxlsWorkbook scenarioWB = new JxlsWorkbook("Scenario");                                        
        JxlsSheet scenario = scenarioWB.addSheet("Scenario");                                          
        scenario.setValue(1, 1, "Date:");                                                              
        scenario.setValue(2, 1, new Date());                                                           
        scenario.setValue(1, 2, "Value:");
        scenario.setValue(2, 2, 3.1415);                                                               
        return scenarioWB;
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
