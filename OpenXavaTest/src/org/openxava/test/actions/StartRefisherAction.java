package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.util.*;
import org.openxava.view.*;

/**
 * 
 * @author Javier Paniza
 */
public class StartRefisherAction extends BaseAction {

	public void execute() throws Exception {
		Refisher r = new Refisher();
		View.setPolisher(r);
		View.setRefiner(r);
	}

}
