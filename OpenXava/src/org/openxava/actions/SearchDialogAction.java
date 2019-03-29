package org.openxava.actions;

import java.util.*;

/**
 * 
 * @author José Luis Dorado
 */

public class SearchDialogAction extends ViewBaseAction implements IChainAction {
	
	public void execute() throws Exception {
		Map values = getView().getValues();
		closeDialog();
		getView().clear();		
		getView().setValues(values);		
	}

	public String getNextAction() throws Exception {
		return getEnvironment().getValue("XAVA_SEARCH_ACTION");
	}
	
}