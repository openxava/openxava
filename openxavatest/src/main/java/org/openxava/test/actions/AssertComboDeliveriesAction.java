package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.model.meta.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class AssertComboDeliveriesAction extends ViewBaseAction {

	public void execute() throws Exception {
		Object delivery = getView().getValue("comboDeliveries");
		if (delivery instanceof Delivery) {
			addMessage("comboDeliveries=" + MetaModel.getForPOJO(delivery).toString(delivery));
		}
		else {
			addError("expected_type", "comboDeliveries", "DeliveryType", "Delivery");
		}
	}

}
