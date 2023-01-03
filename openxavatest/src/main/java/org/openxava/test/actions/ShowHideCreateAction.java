package org.openxava.test.actions;

import org.openxava.actions.*;

public class ShowHideCreateAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		if (isOrderCreated() && isDelivered()) {
			addActions("Order.anAction");
		} else {
			removeActions("Order.anAction");
		}
	}

	private boolean isOrderCreated() {
		return getView().getValue("number") != null;
	}

	private boolean isDelivered() {
		Boolean delivered = (Boolean) getView().getValue("delivered");
		return delivered == null ? false : delivered;
	}

}
