package org.openxava.actions;




/**
 * @author Javier Paniza
 */

public class NavigationAction extends BaseAction implements INavigationAction , IChangeModeAction {
	
	private String nextController;
	private String customView;
	

	public void execute() throws Exception {
	}

	public String[] getNextControllers() throws Exception {
		return new String [] { getNextController() };
	}

	public String getCustomView() throws Exception {
		return customView;
	}
	public void setCustomView(String customView) {
		this.customView = customView;
	}
	

	public String getNextController() {
		return nextController;
	}
	public void setNextController(String nextController) {
		this.nextController = nextController;
	}

	public String getNextMode() {
		return DETAIL;		
	}
		
}
