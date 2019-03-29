package org.openxava.actions;

import javax.inject.*;

import org.openxava.session.*;

/**
 * 
 * @author Javier Paniza 
 */

public class RemoveMyReportAction extends TabBaseAction {
	
	@Inject
	private MyReport myReport;
	
	public void execute() throws Exception {
		String name = getView().getValueString("name");
		if (!getView().isEditable("name")) {
			myReport.remove();					
		}
		
		if (myReport.getAllNames().length > 0) {
			if (getView().isEditable("name")) {
				getView().removeActionForProperty("name", "MyReport.remove");
				getView().removeActionForProperty("name", "MyReport.share");
				getView().addActionForProperty("name", "MyReport.remove");
				getView().addActionForProperty("name", "MyReport.createNew");
			}
			getView().setEditable("name", false);
			getView().setValueNotifying("name", myReport.getLastName());
			myReport = (MyReport) getView().getModel();
			
		}
		else {
			getView().setEditable("name", true);			
			myReport = MyReport.create(getTab()); 
			getView().setModel(myReport);		
			getView().removeActionForProperty("name", "MyReport.createNew");
			getView().removeActionForProperty("name", "MyReport.share");
			getView().removeActionForProperty("name", "MyReport.remove");
		}
		addMessage("report_removed", "'" + name.replace(MyReport.SHARED_REPORT, "") + "'");
	}

}
