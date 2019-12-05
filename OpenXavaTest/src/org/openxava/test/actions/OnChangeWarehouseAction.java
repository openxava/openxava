package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;


/**
 * @author Javier Paniza
 */
public class OnChangeWarehouseAction extends OnChangePropertyBaseAction {
	
	private final static Integer ONE = new Integer(1);

	public void execute() throws Exception {
		// We obtain the warehouse data from entity (also could be view, but we need to test a case with getEntity()) 
		// instead of getNewValue() because getNewValue() only returns a value (the last key property declared), 
		// and the key of warehouse is multiple.
		// The extra (Warehouse) cast is for working in XML version too.
		Warehouse warehouse = (Warehouse) ((Product2) getView().getEntity()).getWarehouse(); // getEntity() to test it in a @OnChange of a reference
		Integer zone = warehouse==null?null:warehouse.getZoneNumber();		
		getView().setHidden("zoneOne", !ONE.equals(zone));
	}

}
