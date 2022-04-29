package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class GoFamilyProductsReportAction extends ViewBaseAction {

	public void execute() throws Exception {
		showDialog(); 
		getView().setModelName("FilterBySubfamily");
		getView().setViewName("Family1");		
		setControllers("FamilyProductsReport"); 
		setNextMode(DETAIL);		 
	}
	
}
