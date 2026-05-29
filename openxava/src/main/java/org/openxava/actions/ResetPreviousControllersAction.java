package org.openxava.actions;

import java.util.*;

import org.openxava.controller.ModuleManager;




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
	
	private Stack<ModuleManager> previousControllers;
	
	
	public void execute() throws Exception {
		previousControllers.clear();
	}

	public Stack<ModuleManager> getPreviousControllers() {
		return previousControllers;
	}

	public void setPreviousControllers(Stack<ModuleManager> previousControllers) {
		this.previousControllers = previousControllers;
	}

}
