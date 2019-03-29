package org.openxava.test.tests;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class SellerWithCustomersAsAggregateTest extends ModuleTestBase {
	
	public SellerWithCustomersAsAggregateTest(String testName) {
		super(testName, "SellerWithCustomersAsAggregate");		
	}
	
	public void testEntityCollectionAsAggregate() throws Exception { 
		execute("CRUD.new");
		setValue("number", "3");
		execute("CRUD.refresh");
		assertValue("name", "ELISEO FERNANDEZ");
		assertCollectionRowCount("customers", 0);
						
		assertCustomerNotExists(66); 
		execute("Collection.new", "viewObject=xava_view_customers");
		assertEditable("number"); 
		assertEditable("name");
		assertValue("number", ""); // Test if clear the fields
		assertValue("type", usesAnnotatedPOJO()?"2":"3"); // Test if execute default value calculators
		
		// Creating
		setValue("number", "66");
		setValue("name", "JUNIT 66");
		setValue("type", usesAnnotatedPOJO()?"0":"1");
		setValue("address.street", "AV. CONSTITUCION");
		setValue("address.zipCode", "46540");
		setValue("address.city", "EL PUIG");
		setValue("address.state.id", "CA");
		
		execute("Collection.save");		
		assertMessage("Customer created and associated to Seller");
		assertCollectionRowCount("customers", 1);
		assertValueInCollection("customers", 0, "name", "Junit 66");
		assertCustomerExists(66);
		
		// Modifiying
		execute("Collection.edit", "row=0,viewObject=xava_view_customers");
		setValue("name", "JUNIT 66x");
		execute("Collection.save");
		assertMessage("Customer modified successfully");
		assertValueInCollection("customers", 0, "name", "Junit 66x");
		
		// Removing
		execute("Collection.edit", "row=0,viewObject=xava_view_customers");
		execute("Collection.remove");
		assertMessage("Association between Customer and Seller has been removed, but Customer is still in database");
		assertCollectionRowCount("customers", 0);
		// Object is not remove from database, only the association is removed
		// This is because is a collection of entities, although is managed as an aggregate
		assertCustomerExists(66); 									
		removeCustomer(66);
	}

	private void removeCustomer(int number) throws Exception {
		XPersistence.getManager().remove(XPersistence.getManager().find(Customer.class, number)); 		
	}

	private void assertCustomerNotExists(int number) {
		if (XPersistence.getManager().find(Customer.class, new Integer(number)) != null) {		
			fail("Customer " + number + " exists and it shouldn't");
		}
				
	}
	
	private void assertCustomerExists(int number) {
		if (XPersistence.getManager().find(Customer.class, new Integer(number)) == null) {		
			fail("Customer " + number + " does not exist and it should");
		}		
	}
	
				
}
