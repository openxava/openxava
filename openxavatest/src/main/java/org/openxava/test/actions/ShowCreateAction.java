package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
public class ShowCreateAction extends BaseAction {

	public void execute() throws Exception {
		addActions("JPACRUD.create"); 
		addActions("JPACRUD.create"); // Twice, to test a bug
	}

}
