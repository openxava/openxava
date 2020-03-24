package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */
public class OnChangeAsEmbed2ValueAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		String value2 = (String) getNewValue();
		if ("HIDE".equals(value2)) {
			System.out.println("[OnChangeAsEmbed2ValueAction.execute] Hidding..."); // tmp
			getView().setHidden("value2", true);
		}
	}

}
