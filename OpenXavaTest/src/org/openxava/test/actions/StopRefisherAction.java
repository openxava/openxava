package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.view.*;

/**
 * tmp 
 * 
 * @author Javier Paniza
 */
public class StopRefisherAction extends BaseAction {

	public void execute() throws Exception {
		View.setPolisher(null);
		View.setRefiner(null);
	}

}
