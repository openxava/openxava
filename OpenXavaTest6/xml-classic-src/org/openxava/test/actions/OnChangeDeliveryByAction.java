package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class OnChangeDeliveryByAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		Number n = (Number) getNewValue();
		int deliveryBy = n==null?0:n.intValue();		
		getView().setHidden("employee", deliveryBy != 1);
		getView().setHidden("carrier", deliveryBy != 2);
	}

}
