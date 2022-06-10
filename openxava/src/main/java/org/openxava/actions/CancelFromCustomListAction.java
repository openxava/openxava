package org.openxava.actions;



import javax.inject.*;

import org.openxava.tab.*;



/**
 * @author Javier Paniza
 */

public class CancelFromCustomListAction extends ViewBaseAction implements ICustomViewAction { 
	
	@Inject 
	private Tab tab;
	
	public void execute() throws Exception {
		closeDialog(); 
		getTab().setModelName(getView().getModelName());		 
	}
	
	public String getCustomView() {
		return PREVIOUS_VIEW; 
	}

	public Tab getTab() {
		return tab;
	}
	public void setTab(Tab tab) {
		this.tab = tab;
	}

}
