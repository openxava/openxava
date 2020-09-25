package org.openxava.test.actions;

import org.openxava.actions.ViewBaseAction;

/**
 * tmp
 * @author Federico Alcantara
 * Created on Sep 23, 2020
 */
public class CarrierRemoveWarehouseAction extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
		getView().setValue("warehouse", null);
	}

}
