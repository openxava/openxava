package org.openxava.test.actions;

import java.time.*;

import org.openxava.actions.*;

public class ObtainDateNewAction extends NewAction {

	@Override
	public void execute() throws Exception {
		super.execute();
		LocalDate date = (LocalDate) getView().getValue("startDate");
	}
	
}
