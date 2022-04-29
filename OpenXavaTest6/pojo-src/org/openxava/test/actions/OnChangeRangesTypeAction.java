package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class OnChangeRangesTypeAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		Ranges.Type type = (Ranges.Type) getNewValue();
		if (type == null) return;
		switch (type) {
			case NUMBERS: getView().setViewName("Numbers"); break;
			case DATES: getView().setViewName("Dates"); break;
			default: getView().setViewName("");
		}
		getView().setValue("type", type);
	}

}
