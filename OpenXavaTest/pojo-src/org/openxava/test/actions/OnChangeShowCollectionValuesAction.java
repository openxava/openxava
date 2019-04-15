package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class OnChangeShowCollectionValuesAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		addMessage("'" + getView().getCollectionValues().toString() + "'"); 
	}

}
