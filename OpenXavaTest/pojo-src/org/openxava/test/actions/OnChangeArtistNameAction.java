package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza 
 */
public class OnChangeArtistNameAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		String name = getView().getValueString("name");
		if (Is.emptyString(name)) return;
		showDialog();
		getView().setTitleId("sure_change_name");
		getView().setModelName("Name");
		getView().setValue("name", name);
		setControllers("Dialog");		
	}

}
