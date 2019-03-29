package org.openxava.actions;

import java.util.*;




/**
 * Delete the stack of all visited controllers. <p>
 * 
 * After you execute this action if you try to
 * return to the previous controller then you return
 * to the default controllers of module.<br>
 *  
 * @author Javier Paniza
 */


public class ResetPreviousControllersAction extends BaseAction {
	
	private Stack previousControllers;
	
	
	public void execute() throws Exception {
		previousControllers.clear();
	}

	public Stack getPreviousControllers() {
		return previousControllers;
	}

	public void setPreviousControllers(Stack previousControllers) {
		this.previousControllers = previousControllers;
	}

}
