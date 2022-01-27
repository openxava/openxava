package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * tmr
 * 
 * @author Javier Paniza
 */
public class RemoveThirdActionAction extends BaseAction {

	public void execute() throws Exception {
		removeActions("ColorSub.thirdAction");
	}
	
	

}
