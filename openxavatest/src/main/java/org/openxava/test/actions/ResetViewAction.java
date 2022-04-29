package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Jeromy Altuna
 */
public class ResetViewAction extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
			getView().reset();
	}
}
