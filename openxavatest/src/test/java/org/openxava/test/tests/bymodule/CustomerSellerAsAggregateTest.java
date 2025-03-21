package org.openxava.test.tests.bymodule;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class CustomerSellerAsAggregateTest extends ModuleTestBase {
	
	public CustomerSellerAsAggregateTest(String testName) {
		super(testName, "CustomerSellerAsAggregate");				
	}
	

	public void testReferenceToEntityAsAggregate() throws Exception { 
		// Creating customer and its seller at once
		execute("CRUD.new");
		setValue("number", "66");
		setValue("name", "JUNIT CUSTOMER 66");
		setValue("address.street", "DOCTOR PESSET");
		setValue("address.zipCode", "46540");
		setValue("address.city", "EL PUIG");
		setValue("address.state.id", "CA");
		
		setValue("seller.number", "66");
		setValue("seller.name", "SELLER CREATED FROM CUSTOMER");
		
		execute("CRUD.save");
		assertNoErrors(); 
		
		// Searching to verify
		assertValue("name", "");
		setValue("number", "66");
		execute("CRUD.refresh");
		
		assertValue("name", "Junit Customer 66");
		assertValue("seller.number", "66"); 
		assertValue("seller.name", "SELLER CREATED FROM CUSTOMER");
		
		// Modifiying
		setValue("seller.name", "SELLER MODIFIED FROM CUSTOMER");
		
		execute("CRUD.save");
		assertNoErrors(); 
		
		// Searching to verify
		assertValue("name", "");
		setValue("number", "66");
		execute("CRUD.refresh");
		
		assertValue("name", "Junit Customer 66");
		assertValue("seller.number", "66");
		assertValue("seller.name", "SELLER MODIFIED FROM CUSTOMER");
		
		// Removing
		execute("CRUD.delete");
		assertNoErrors();
		assertMessage("Customer deleted successfully");
		
		// Asserting that seller is not removed
		// Although at IU level 'seller' behaves as aggregate, actually it's a reference
		// to entity, therefore it cannot be removed automatically, because maybe reference
		// from other place.
		assertCustomerNotExist(66);
		assertSellerExist(66);
		deleteSeller(66);
		
		// Test that AccessTracker works for references @AsEmbedded
		LogTrackerUtils.assertAccessLog( 
			"CREATED: user=admin, model=Seller, key={number=66}",
			"CREATED: user=admin, model=Customer, key={number=66}",
			"CONSULTED: user=admin, model=Customer, key={number=66}",
			// The MODIFIED includes the key for the key, not all the values (it was a bug)
			"MODIFIED: user=admin, model=Seller, key={number=66}, changes=Name: SELLER CREATED FROM CUSTOMER --> SELLER MODIFIED FROM CUSTOMER",
			"CONSULTED: user=admin, model=Customer, key={number=66}",
			"REMOVED: user=admin, model=Customer, key={number=66}",
			"CONSULTED: user=admin, model=Customer, key={number=1}"			
		);		
	}


	private void deleteSeller(int number) throws Exception {		
		XPersistence.getManager().remove(XPersistence.getManager().find(Seller.class, number));		
	}

	private void assertSellerExist(int number) {		
		if (XPersistence.getManager().find(Seller.class, new Integer(number)) == null) {					
			fail("Seller " + number + " does not exist, and it should");
		}		
	}

	private void assertCustomerNotExist(int number) {
		if (XPersistence.getManager().find(Customer.class, new Integer(number)) != null) {
			fail("Customer " + number + " exists, and it shouldn't");
		}
	}
		
}
