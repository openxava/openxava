package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class OnChangeProduct3DescriptionAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {				
		if (getNewValue() == null) {			
			return;
		}
		String description = (String) getNewValue();
		if ("ANATHEMA".equals(description)) {			
			getView().setEditable(false);			
			return;
		}								
	}
}
