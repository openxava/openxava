package org.openxava.actions;

/**
 * 
 * @author Javier Paniza
 */

public class SearchReadOnlyAction extends SearchByViewKeyAction {
		
	public void execute() throws Exception {
		super.execute();
		getView().setEditable(false);
	}

}
