package org.openxava.test.actions;

import org.openxava.actions.*;

public class ChangeActiveYearAction extends ViewBaseAction {
	
	private Integer activeYear;
	
	public void execute() throws Exception {
		Integer year = (Integer) getView().getValue("year");		
		setActiveYear(year);	
		addMessage("active_year_set", year);
	}

	public Integer getActiveYear() {
		return activeYear;
	}

	public void setActiveYear(Integer activeYear) {
		this.activeYear = activeYear;
	}

}
