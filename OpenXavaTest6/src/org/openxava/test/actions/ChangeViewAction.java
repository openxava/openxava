package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class ChangeViewAction extends ViewBaseAction {
	
	private String newView;

	public void execute() throws Exception {
		getView().setViewName(getNewView());
	}

	public String getNewView() {
		return newView;
	}
	public void setNewView(String newView) {
		this.newView = newView;
	}
}
