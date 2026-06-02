package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */
public class OnChangePhoneAreaCodeAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		if (Integer.valueOf(34).equals(getNewValue())) {
			getView().setHidden("phoneExtension", true);
			
		}
		else {
			getView().setHidden("phoneExtension", false);			
		}
	}

}
