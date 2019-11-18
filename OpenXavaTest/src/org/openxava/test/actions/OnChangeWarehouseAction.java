package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.test.model.*;


/**
 * @author Javier Paniza
 */
public class OnChangeWarehouseAction extends OnChangePropertyBaseAction {
	
	private final static Integer ONE = new Integer(1);

	public void execute() throws Exception {
		// tmp ini
		/*
		System.out.println("[OnChangeWarehouseAction.execute] 7 "); // tmp
		Object e = getView().getEntity();
		if (e != null) {
			System.out.println("[OnChangeWarehouseAction.execute] e.getClass()=" + e.getClass()); // tmp
			System.out.println("[OnChangeWarehouseAction.execute] e.getZoneNumber()=" + ((Product2) e).getWarehouse().getZoneNumber()); // tmp
		}
		*/
		// tmp fin
		// We obtain the warehouse data from view instead of getNewValue() 
		// because getNewValue() only returns a value (the last key property declared), 
		// and the key of warehouse is multiple.
		/* tmp
		Map warehouse = (Map) getView().getValue("warehouse");
		Integer zone = (Integer) warehouse.get("zoneNumber");		
		System.out.println("[OnChangeWarehouseAction.execute] zone=" + zone); // tmp
		getView().setHidden("zoneOne", !ONE.equals(zone));
		*/
		// tmp ini
		Warehouse warehouse = ((Product2) getView().getEntity()).getWarehouse();
		Integer zone = warehouse==null?null:warehouse.getZoneNumber();		
		getView().setHidden("zoneOne", !ONE.equals(zone));
		// tmp fin
	}

}
