package org.openxava.actions;

/**
 * 
 * @author Javier Paniza
 */

public class CancelToDefaultControllersAction extends CancelAction {
	
	public String [] getNextControllers() {
		return DEFAULT_CONTROLLERS;		
	}
			
}
