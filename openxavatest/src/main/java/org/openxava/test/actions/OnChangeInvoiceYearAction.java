package org.openxava.test.actions;

import javax.inject.*;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
public class OnChangeInvoiceYearAction extends OnChangePropertyBaseAction {
	
	@Inject
	private int activeYear; 

	public void execute() throws Exception {
		int year = ((Number) getNewValue()).intValue();
		if (year != activeYear) {
			addError("only_active_year_allowed", Integer.toString(activeYear), Integer.toString(year));
		}
	}
	
	

}
