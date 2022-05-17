package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.jpa.*;

/**
 * 
 * @author Javier Paniza 
 */

public class CreateUsingJPAAction extends ViewBaseAction {

	public void execute() throws Exception {
		Object e = getView().getEntity();
		XPersistence.getManager().persist(e);
	}	

}
