package org.openxava.test.actions;

import org.openxava.actions.*;

public class ShowActiveYearAction extends ViewBaseAction {
	
	private Integer activeYear;
	
	public void execute() throws Exception {
		getView().setValue("year", getActiveYear());
	}

	public Integer getActiveYear() {
		return activeYear;
	}

	public void setActiveYear(Integer activeYear) {
		this.activeYear = activeYear;
	}

}
