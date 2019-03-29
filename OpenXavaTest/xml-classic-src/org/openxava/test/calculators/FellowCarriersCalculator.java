package org.openxava.test.calculators;

import java.rmi.*;
import java.util.*;

import org.openxava.calculators.*;
import org.openxava.hibernate.XHibernate;
import org.openxava.test.model.*;

/**
 * @author Javier Paniza
 */
public class FellowCarriersCalculator implements IModelCalculator {

	private ICarrier carrier;

	public Object calculate() throws Exception {
		if (carrier == null || carrier.getWarehouse() == null) return Collections.EMPTY_LIST; 
		int warehouseZoneNumber = carrier.getWarehouse().getZoneNumber();
		int warehouseNumber = carrier.getWarehouse().getNumber();
		org.hibernate.Session session = XHibernate.getSession();		
		org.hibernate.Query query = session.createQuery("from Carrier as o where " +
				"o.warehouse.zoneNumber = :warehouseZone AND " +
				"o.warehouse.number = :warehouseNumber AND " +
				"NOT (o.number = :number)  order by o.number"); 
		query.setInteger("warehouseZone", warehouseZoneNumber);
		query.setInteger("warehouseNumber", warehouseNumber);
		query.setInteger("number", carrier.getNumber());
		return query.list();
	}

	public void setModel(Object entity) throws RemoteException {
		carrier = (ICarrier) entity;		
	}

}
 