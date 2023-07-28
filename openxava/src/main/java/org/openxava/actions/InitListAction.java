package org.openxava.actions;

import javax.inject.*;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.openxava.tab.*;
import org.openxava.util.*;

/**
 * @author Javier Paniza
 */
public class InitListAction extends TabBaseAction {
	
	private static Log log = LogFactory.getLog(InitListAction.class);
	
	@Inject
	private Tab mainTab;

	public void execute() throws Exception {
		setMainTab(getTab());
		executeAction("ListFormat.select");
		if (isListEmpty()) { 
			String newAction = getQualifiedActionIfAvailable("new"); 
			if (newAction != null) {
				executeAction(newAction);
				return;
			}
		}		
		getTab().setNotResetNextTime(true); // To avoid executing the initial select twice
	}
	
	private boolean isListEmpty() {
		try {
			int rowCount = getTab().getTotalSize();
			if (rowCount < 0) {
				XPersistence.rollback();
				return true; // We assume true, so the module works in detail mode even if there is database errors, like the table does not exist
			}
			return rowCount == 0;
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("tab_result_size_error"), ex);
			return true; // We assume true, so the module works in detail mode even if there is database errors, like the table does not exist
		}
	}

	public Tab getMainTab() {
		return mainTab;
	}
	public void setMainTab(Tab mainTab) {
		this.mainTab = mainTab;
	}
	
}
