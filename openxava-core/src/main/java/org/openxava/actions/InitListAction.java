package org.openxava.actions;

import javax.inject.*;
import org.openxava.tab.*;

/**
 * @author Javier Paniza
 */
public class InitListAction extends TabBaseAction { 
	
	@Inject
	private Tab mainTab;

	public void execute() throws Exception {
		setMainTab(getTab());
		executeAction("ListFormat.select");
		
		if (getTab().getTableModel().getRowCount() == 0 || getTab().getTableModel().getColumnCount() == 0) { 
			String newAction = getQualifiedActionIfAvailable("new"); 
			if (newAction != null) {
				executeAction(newAction);
				return;
			}
		}		
		getTab().setNotResetNextTime(true); // To avoid executing the initial select twice
	}

	public Tab getMainTab() {
		return mainTab;
	}
	public void setMainTab(Tab mainTab) {
		this.mainTab = mainTab;
	}
	
}
