package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.view.*;

/**
 * 
 * @author Javier Paniza 
 */

public class RemoveTranslateNameAction extends ViewBaseAction {

	public void execute() throws Exception {
		View v = getView().getSubview("fellowCarriers");
		addMessage("Original fellows actions=" + v.getActionsNamesList());
		v.removeListAction("Carrier.translateName");		 
	}

}
