package org.openxava.test.actions;

import java.util.*;

import javax.inject.Inject;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.tab.Tab;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 *
 */
public class AssignSellerToSelectedCustomersAction extends TabBaseAction {

	public void execute() throws Exception {
		int sellerNumber = getView().getValueInt("seller.number");
		Map newSellerValues = new HashMap();
		if (sellerNumber == 0) {
			newSellerValues.put("seller", null);
		}
		else {
			Maps.putValueFromQualifiedName(newSellerValues, "seller.number", sellerNumber);
		}
		for (Map customerKey: getSelectedKeys()) {
			MapFacade.setValues("Customer", customerKey, newSellerValues);
		}
		closeDialog();
	}

}
