package org.openxava.actions;



import org.openxava.tab.*;

/**
 * @author Javier Paniza
 */

public class RestoreDefaultColumnsAction extends ViewBaseAction implements INavigationAction, IChangeModeAction  {

	private Tab tab;	

	public void execute() throws Exception {		
		getTab().restoreDefaultProperties();
		closeDialog(); 
	}
	
	public Tab getTab() {
		return tab;
	}
	public void setTab(Tab tab) {
		this.tab = tab;
	}
	
	public String[] getNextControllers() throws Exception {
		return PREVIOUS_CONTROLLERS; 
	}
	

	public String getCustomView() throws Exception {
		return PREVIOUS_VIEW; 
	}

	
	public String getNextMode() {
		return PREVIOUS_MODE; 
	}
	
}
