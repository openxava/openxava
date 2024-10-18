package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;

public class ObtainDateTimeAction extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
		Date date = (Date) getView().getValue("time");
		if (date != null) {
			addMessage("Date obtained");
		}
	}
	
}
