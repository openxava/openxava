package org.openxava.test.tests;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class CustomerContactPersonAsAggregate3LevelsTest extends ModuleTestBase {
	
	public CustomerContactPersonAsAggregate3LevelsTest(String testName) {
		super(testName, "CustomerContactPersonAsAggregate3Levels");		
	}
	
	public void testAsAggregate3Levels() throws Exception {
		execute("CRUD.new");
		execute("Reference.search", "keyProperty=xava.CustomerContactPerson.customer.seller.level.id");
		assertListColumnCount(2); 
		assertLabelInList(0, "Id"); 
		assertLabelInList(1, "Description");
		assertValueInList(0, 0, "A");
		assertValueInList(0, 1, "MANAGER");
	}
		
	public void testRemovingWithReferenceAsKeyAsEmbedded() throws Exception {  
		int count = getListRowCount();
		execute("CRUD.new");
		setValue("customer.number", "66");
		setValue("customer.name", "Junit Customer");
		setValue("customer.address.street", "Junit Street");
		setValue("customer.address.zipCode", "46540");
		setValue("customer.address.city", "EL PUIG");
		setValue("customer.address.state.id", "CA");
		setValue("customer.seller.number", "66");
		setValue("customer.seller.name", "JUNIT SELLER");
		setValue("name", "Junit Contact");
		execute("CRUD.save");
		assertNoErrors();
		setValue("customer.number", "66");		
		assertValue("name", "");
		execute("CRUD.refresh");	
		assertValue("name", "Junit Contact");
		execute("CRUD.delete");
		assertNoErrors();
		execute("Mode.list");
		assertListRowCount(count);
		deleteSeller(66);
		deleteCustomer(66);
	}
	
	private void deleteSeller(int number) throws Exception {		
		XPersistence.getManager().remove(XPersistence.getManager().find(Seller.class, number));		
	}
	
	private void deleteCustomer(int number) throws Exception {		
		XPersistence.getManager().remove(XPersistence.getManager().find(Customer.class, number));		
	}
	
}
