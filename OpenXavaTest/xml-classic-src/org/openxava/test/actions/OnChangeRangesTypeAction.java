package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class OnChangeRangesTypeAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		Number type = (Number) getNewValue();
		if (type == null) return;		
		switch (((Number) type).intValue()) {
			case 1: getView().setViewName("Numbers"); break;
			case 2: getView().setViewName("Dates"); break;
			default: getView().setViewName("");
		}
		getView().setValue("type", type);
	}

}
