package org.openxava.actions;

import java.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class SearchExecutingOnChangeAction extends SearchByViewKeyAction {
	
	protected void setValuesToView(Map values) throws Exception {		
		getView().setValuesExecutingOnChangeActions(values);		
	}

}
