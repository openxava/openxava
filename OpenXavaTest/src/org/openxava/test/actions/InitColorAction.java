package org.openxava.test.actions;

import javax.inject.*;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class InitColorAction extends BaseAction {
	
	@Inject
	private int initColorTimes; // tmp

	public void execute() throws Exception {
		System.out.println("[InitColorAction.execute] "); // tmp
		// tmp addMessage("color_initiated");
		addMessage("color_initiated", (++initColorTimes)); // tmp
	}
	
}
