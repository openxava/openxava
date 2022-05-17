package org.openxava.test.actions;

import javax.inject.*;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class InitColorAction extends BaseAction {
	
	@Inject
	private int initColorTimes; 

	public void execute() throws Exception {
		addMessage("color_initiated", (++initColorTimes)); 
	}
	
}
