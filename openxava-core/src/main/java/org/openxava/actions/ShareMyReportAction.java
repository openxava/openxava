package org.openxava.actions;

import javax.inject.*;

import org.apache.commons.logging.*;
import org.openxava.session.*;

/**
 * It removes the initial report and it makes a shared report.<br>
 * 
 * Create on 13/02/2014 (12:41:15)
 * @author Ana Andres
 */
public class ShareMyReportAction extends ViewBaseAction {
	private static Log log = LogFactory.getLog(ShareMyReportAction.class);
	
	@Inject
	private MyReport myReport; 
	
	public void execute() throws Exception {
		myReport.remove();
		myReport.save(true);
		getView().setValueNotifying("name", myReport.getName() + MyReport.SHARED_REPORT);
	}
}
