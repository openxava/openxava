package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.view.*;

/**
 * 
 * @author Javier Paniza 
 */

public class AddTranslateNameAction extends ViewBaseAction {

	public void execute() throws Exception {
		View v = getView().getSubview("fellowCarriers");		
		v.addListAction("Carrier.translateName");
		addMessage("Final fellows actions=" + v.getActionsNamesList());
	}

}
