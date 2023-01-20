package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;



/**
 * @author Javier Paniza
 */

public class OnChangeCustomerNameAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		String value = (String) getNewValue();
		if (value == null) return;
		if (value.startsWith("Javi")) {
			getView().setValue("type", Customer.Type.STEADY);
		}	
		// tmr ini
		else if (value.equals("Pedro")) {
			getView().setValueNotifying("seller.number", 2);
			getView().setValueNotifying("alternateSeller.number", 1);
		}		
		// tmr fin
	}

}
