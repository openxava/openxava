package org.openxava.actions;

import javax.inject.*;
import org.openxava.tab.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

public class GoDetailAction extends BaseAction implements IChangeModeAction, IChainAction, IChangeControllersAction {
	
	private String nextSection;
	private String nextAction;
	@Inject
	private Tab tab;
	

	public String getNextMode() {		
		return nextSection;
	}	
	
	public void execute() throws Exception {
		if (getTab().getTotalSize() <= 0) {
			String newAction = getQualifiedActionIfAvailable("new"); 
			if (newAction == null) { 
				addError("no_detail_no_elements");
				nextSection = IChangeModeAction.LIST;
				nextAction = null;
			}
			else {
				nextSection = IChangeModeAction.DETAIL;
				nextAction = newAction;
			}
		}
		else {
			nextSection = IChangeModeAction.DETAIL;
		}
	}

	public String getNextAction() throws XavaException {
		if (Is.emptyString(nextAction) && IChangeModeAction.DETAIL.equals(nextSection)) return getEnvironment().getValue("XAVA_SEARCH_ACTION");		
		return nextAction;
	}
	

	public void setNextAction(String string) {
		nextAction = string;
	}

	public String[] getNextControllers() {		
		return null; // Null for set all default actions
	}

	public Tab getTab() {
		return tab;
	}

	public void setTab(Tab tab) {
		this.tab = tab;
	}

}
