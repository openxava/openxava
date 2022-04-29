package org.openxava.test.actions;

import javax.persistence.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;

/**
 * 
 * @author Javier Paniza
 */
public class NewWarehouseAction extends NewAction {

	public void execute() throws Exception {
		Query query = XPersistence.getManager().createQuery("select count(*) from Warehouse");
		super.execute(); // This execute() does not close the persistence manager and transaction		 
		Long warehouseCount = (Long) query.getSingleResult();
		addMessage("warehouse_count", warehouseCount); 
	}
	
}
