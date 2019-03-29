package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;


/**
 * @author Javier Paniza
 */
public class OnChangeWarehouseAction extends OnChangePropertyBaseAction {
	
	private final static Integer ONE = new Integer(1);

	public void execute() throws Exception {	
		// We obtain the warehouse data from view instead of getNewValue() 
		// because getNewValue() only returns a value (the last key property declared), 
		// and the key of warehouse is multiple.
		Map warehouse = (Map) getView().getValue("warehouse");
		Integer zone = (Integer) warehouse.get("zoneNumber");		
		getView().setHidden("zoneOne", !ONE.equals(zone));		
	}

}
