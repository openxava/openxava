package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class OnChangeFamilyAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {		
		if (getNewValue() == null) getView().setValue("comments", "");
		else getView().setValue("comments", "Family changed");		
	}
}
