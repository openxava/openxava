package org.openxava.actions;

import org.openxava.web.*;

/**
 * 
 * @author Javier Paniza
 */
public class ReleaseChartAction extends ViewBaseAction { 
	
	public void execute() throws Exception {
		Charts.release(getRequest());
		getView().setModelName(getManager().getModelName());
		getView().setViewName(getManager().getXavaViewName());
	}

}
