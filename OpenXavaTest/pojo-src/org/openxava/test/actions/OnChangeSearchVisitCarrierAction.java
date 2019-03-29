package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class OnChangeSearchVisitCarrierAction extends OnChangeSearchAction {
	
	public void execute() throws Exception {
		int number = ((Number) getNewValue()).intValue();
		if (number > 10) number -= 10;
		getView().setValue("number", number);
		super.execute();
	}

}
