package org.openxava.actions;

import org.openxava.web.*;

/**
 * 
 * @author Javier Paniza
 */
public class ReleaseChartAction extends BaseAction { 
	
	public void execute() throws Exception {
		Charts.release(getRequest());
	}

}
