package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * Hide or show a property, reference, collection, group or section. 
 * 
 * @author Javier Paniza
 */

public class HideShowAction extends ViewBaseAction {

	private boolean hide;
	private String name;

	public void execute() throws Exception {
		getView().setHidden(name, hide);		
	}

	public boolean isHide() {
		return hide;
	}

	public void setHide(boolean b) {
		hide = b;
	}

	public String getName() {
		return name;
	}

	public void setName(String string) {
		name = string;
	}

}
