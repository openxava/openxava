package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class MyGoListAction extends GoListAction {
	
	public void execute() throws Exception {
		super.execute();
		addMessage("back_to_list"); 
	}

}
