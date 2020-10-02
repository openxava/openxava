package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class SetActingLevelAAction extends ViewBaseAction {

	public void execute() throws Exception {
		ActingLevel actingLevel = XPersistence.getManager().find(ActingLevel.class, "A");
		getView().setModel(actingLevel);
	}
	
}
