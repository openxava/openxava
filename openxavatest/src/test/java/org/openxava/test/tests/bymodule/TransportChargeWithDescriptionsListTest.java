package org.openxava.test.tests.bymodule;

import org.openxava.model.meta.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class TransportChargeWithDescriptionsListTest extends ModuleTestBase {
	
	public TransportChargeWithDescriptionsListTest(String testName) {
		super(testName, "TransportChargeWithDescriptionsList");
	}
		
	public void testNestedCompositeKeysInDescriptionsList() throws Exception { 
		deleteAllTransportCharges(); 
		assertListRowCount(0);  
		execute("CRUD.new");
		Delivery delivery = createDelivery();
		String key = MetaModel.getForPOJO(delivery).toString(delivery);		
		setValue("delivery.KEY", key);
		String [][] deliveries = {
			{ "", "" },
			{ "[.2.2004.666.1.]", "DELIVERY JUNIT 666 2/22/2004" },
			{ "[.9.2004.666.1.]", "DELIVERY JUNIT 666 2/22/2004" },
			{ "[.10.2004.666.1.]", "DELIVERY JUNIT 666 2/22/2004" },
			{ "[.11.2004.666.1.]", "DELIVERY JUNIT 666 2/22/2004" },
			{ "[.12.2004.666.1.]", "DELIVERY JUNIT 666 2/22/2004" },
			{ "[.2.2004.777.2.]", "FOR TEST SEARCHING BY DESCRIPTION 6/23/2006" }
		};
		assertValidValues("delivery.KEY", deliveries);
		setValue("amount", "324.28"); 
		execute("CRUD.save");
		assertNoErrors(); 
		execute("Mode.list");
		assertListRowCount(1);
		execute("List.viewDetail", "row=0");
		assertValue("delivery.KEY", key);
		assertDescriptionValue("delivery.KEY", "FOR TEST SEARCHING BY DESCRIPTION 6/23/2006"); 
		assertValue("amount", "324.28");
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	private Delivery createDelivery() {		
		Invoice invoice = new Invoice();
		invoice.setYear(2004);
		invoice.setNumber(2);
		DeliveryType type = new DeliveryType();
		type.setNumber(2);
		Delivery delivery = new Delivery();
		delivery.setInvoice(invoice);
		delivery.setType(type);
		delivery.setNumber(777);
		return delivery;
	}
	
	private void deleteAllTransportCharges() throws Exception {
		if (existsAction("Mode.list")) {
			execute("Mode.list"); 
		}
		else {
			checkAll();
			execute("CRUD.deleteSelected");			
		}
	}
	
}
