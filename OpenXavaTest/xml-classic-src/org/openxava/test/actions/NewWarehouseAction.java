package org.openxava.test.actions;

import org.hibernate.*;
import org.openxava.actions.*;
import org.openxava.hibernate.*;

/**
 * 
 * @author Javier Paniza
 */
public class NewWarehouseAction extends NewAction {

	public void execute() throws Exception {
		Query query = XHibernate.getSession().createQuery("select count(*) from Warehouse");
		super.execute(); // This execute() does not close the persistence manager and transaction		 
		Long warehouseCount = (Long) query.uniqueResult();
		addMessage("warehouse_count", warehouseCount); 
	}
	
}
