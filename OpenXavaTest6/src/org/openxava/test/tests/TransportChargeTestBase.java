package org.openxava.test.tests;

import java.math.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

abstract public class TransportChargeTestBase extends ModuleTestBase {
	
	private TransportCharge charge1;
	private TransportCharge charge2;
		
	public TransportChargeTestBase(String testName, String moduleName) {
		super(testName, moduleName);		
	}
	
	
	protected void deleteAll() throws Exception {
		XPersistence.getManager().createQuery("delete from TransportCharge").executeUpdate();
		XPersistence.commit(); 
		
	}	
	
	protected void createSome() throws Exception {
		Collection deliveries = XPersistence.getManager().createQuery("select d from Delivery as d").getResultList();	
		assertTrue("At least 2 deliveries is required to run this test", deliveries.size() > 1);
		Iterator it = deliveries.iterator();
		
		Delivery delivery1 = (Delivery) it.next();		
		charge1 = new TransportCharge();
		
		charge1.setDelivery(delivery1);
		charge1.setAmount(new BigDecimal("100.00"));
		XPersistence.getManager().persist(charge1);
						
		Delivery delivery2 = (Delivery) it.next();		
		charge2 = new TransportCharge();
		charge2.setDelivery(delivery2);
		charge2.setAmount(new BigDecimal("200.00"));			
		XPersistence.getManager().persist(charge2);
		XPersistence.commit(); 
		charge1 = XPersistence.getManager().merge(charge1); 
		charge2 = XPersistence.getManager().merge(charge2);
	}
	
	protected TransportCharge getCharge1() {
		return charge1;
	}

	protected TransportCharge getCharge2() {
		return charge2;
	}

}
