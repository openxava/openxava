package org.openxava.actions;

/**
 * 
 * @since 5.9
 * @author Javier Paniza
 */

public class InitViewAction extends ViewBaseAction {
	
	public void execute() throws Exception {
		if (getManager().isDetailMode() && getView().isEditable()) {
			getView().setEditable(true);
			getView().setKeyEditable(true);
		}
		else {
			getView().setEditable(false);
			getView().setKeyEditable(false);
		}
	}
	
}
