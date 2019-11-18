package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza 
 */
public class OnChangeArtistNameAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		String name = ((Artist) getView().getEntity()).getName(); 
		if (Is.emptyString(name)) return;
		showDialog();
		getView().setTitleId("sure_change_name");
		getView().setModelName("Name");
		getView().setValue("name", name);
		setControllers("Dialog");		
	}

}
