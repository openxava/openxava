package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 *
 */
public class AssignSellerToSelectedCustomersAction extends TabBaseAction {

	public void execute() throws Exception {
		int sellerNumber = getView().getValueInt("seller.number");
		System.out.println("[AssignSellerToSelectedCustomersAction.execute] sellerNumber=" + sellerNumber); // tmr
		if (sellerNumber == 0) {
			addError("Es obligado escoger un vendedor"); // tmr i18n
		}
		Map newSellerValues = new HashMap();
		Maps.putValueFromQualifiedName(newSellerValues, "seller.number", sellerNumber);
		System.out.println("[AssignSellerToSelectedCustomersAction.execute] "); // tmr
		for (Map customerKey: getSelectedKeys()) {
			System.out.println("[AssignSellerToSelectedCustomersAction.execute] customerKey=" + customerKey); // tmr
			MapFacade.setValues("Customer", customerKey, newSellerValues);
		}
		closeDialog();
	}

}
