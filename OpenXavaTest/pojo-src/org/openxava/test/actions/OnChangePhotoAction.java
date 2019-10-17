package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 *
 */
public class OnChangePhotoAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		addMessage("photo_changed"); 
	}

}
