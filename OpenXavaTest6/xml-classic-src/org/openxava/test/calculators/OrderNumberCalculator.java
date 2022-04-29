package org.openxava.test.calculators;

import java.rmi.*;

import org.hibernate.*;
import org.openxava.calculators.*;
import org.openxava.hibernate.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javier Paniza
 */

public class OrderNumberCalculator implements IModelCalculator {
	
	private IOrder order;

	public Object calculate() throws Exception {
		Session session = XHibernate.createSession();
		Transaction tx = session.beginTransaction();
		try {
			Query query = session
				.createQuery("select max(o.number) from Order o " + 
					"where o.year = :year");
			query.setParameter("year", new Integer(order.getYear()));				
			Integer lastNumber = (Integer) query.uniqueResult();
			order.setNumber(lastNumber == null?1:lastNumber.intValue() + 1);
		}
		finally {
			tx.commit();
			session.close();
		}
		return null;
	}
	
	public void setModel(Object model) throws RemoteException {
		this.order = (IOrder) model;
	}

}
