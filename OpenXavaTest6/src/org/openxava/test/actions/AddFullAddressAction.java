package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.view.*;

public class AddFullAddressAction extends ViewBaseAction {

	public void execute() throws Exception {		
		String fullAddress = getView().getValueString("fullAddress");
		String [] tokens = fullAddress.split(" ");
		View addressView = getPreviousView().getSubview("address");
		String [] properties = { "state.id", "city", "zipCode", "street" };
		int iTokens = tokens.length;		
		for (int iProperties = 0; iProperties < 4 && iTokens > 0; iProperties++) {			
			addressView.setValue(properties[iProperties], tokens[--iTokens]);
		}
		StringBuffer street = new StringBuffer();
		for (int i = 0; i <= iTokens; i++) {
			street.append(tokens[i]);
			street.append(' ');
		}
		addressView.setValue("street", street.toString().trim());
		
		closeDialog(); 
	}

}
