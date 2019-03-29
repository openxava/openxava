package org.openxava.actions;




/**
 * @author Javier Paniza
 */

public class SetCustomViewAction extends BaseAction implements ICustomViewAction {
	
	private String customView;
	
	
	public void execute() throws Exception {
	}


	public String getCustomView() {
		return customView;
	}

	public void setCustomView(String customView) {
		this.customView = customView;
	}
	
}
