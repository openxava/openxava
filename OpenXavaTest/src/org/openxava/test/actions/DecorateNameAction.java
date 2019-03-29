package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.util.*;


/**
 * @author Javier Paniza
 */

public class DecorateNameAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		String name = (String) getView().getValue("name");		
		if (Is.emptyString(name)) return;
		if (name.startsWith("DON")) return;
		name = "DON " + name;		
		getView().setValue("name", name);						
	}

}
