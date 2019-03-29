package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class SetControllersAction extends BaseAction {
	
	private String controller1;
	private String controller2;
	
	public void execute() throws Exception {
		if (controller1.equals("DEFAULT")) setDefaultControllers();
		else if (controller1.equals("PREVIOUS")) returnToPreviousControllers();
		else setControllers(controller1, getController2());	
	}

	public String getController1() {
		return controller1;
	}

	public void setController1(String controller) {
		this.controller1 = controller;
	}

	public void setController2(String controller2) {
		this.controller2 = controller2;
	}

	public String getController2() {
		return controller2;
	}

}
