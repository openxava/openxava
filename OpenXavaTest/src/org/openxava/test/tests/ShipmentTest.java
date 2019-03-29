package org.openxava.test.tests;

import javax.persistence.*;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class ShipmentTest extends ModuleTestBase {
	
	public ShipmentTest(String testName) {
		super(testName, "Shipment");		
	}
	
	public void testDateTimeCombinedSpanishFormat() throws Exception { 
		setLocale("es");
		execute("List.viewDetail", "row=0");
		setValue("time", "30/6/14");
		execute("CRUD.save");
		assertValue("time", "");
		execute("Navigation.first");
		assertValue("time", "30/06/2014 00:00");

		setValue("time", "30/6/14 13:30");
		execute("CRUD.save");
		assertValue("time", "");
		execute("Navigation.first");
		assertValue("time", "30/06/2014 13:30");
		
		setValue("time", "30/6/2014");
		execute("CRUD.save");
		assertValue("time", "");
		execute("Navigation.first");
		assertValue("time", "30/06/2014 00:00");

		setValue("time", "30/6/2014 13:30");
		execute("CRUD.save");
		assertValue("time", "");
		execute("Navigation.first");
		assertValue("time", "30/06/2014 13:30");		
		
		setValue("time", "");
		execute("CRUD.save");
		assertNoErrors();
	}
	
	public void testCreateReferenceFromCreatingReference() throws Exception { 
		execute("CRUD.new");
		execute("Reference.createNew", "model=CustomerContactPerson,keyProperty=xava.Shipment.customerContactPerson.name");
		execute("Reference.createNew", "model=Customer,keyProperty=xava.CustomerContactPerson.customer.number");
		
		setValue("Customer", "number", "66");
		setValue("Customer", "type", "1");
		setValue("Customer", "name", "Customer Junit From Shipment");
		setValue("Customer", "address.street", "JUNIT STREET");
		setValue("Customer", "address.zipCode", "55555");
		setValue("Customer", "address.city", "JUNIT CITY");
		setValue("Customer", "address.state.id", "CA");
		execute("NewCreation.saveNew");
		assertNoErrors();
		setValue("CustomerContactPerson", "name", "JUNIT CONTACT PERSON FROM SHIPMENTS");
		execute("NewCreation.saveNew");
		assertNoErrors();
		assertValue("customerContactPerson.name", "Junit Contact Person From Shipments");
		assertValue("customerContactPerson.customer.number", "66");
		assertValue("customerContactPerson.customer.name", "Customer Junit From Shipment");
		
		deleteCustomerAndContactPerson(66);
	}
		
	public void testCreateReadDeleteWithConverterInKey() throws Exception { 
		// Create
		execute("CRUD.new");
		
		setValue("type", usesAnnotatedPOJO()?"0":"1");
		setValue("mode", usesAnnotatedPOJO()?"1":"2");
		setValue("number", "66");
		setValue("description", "JUNIT SHIPMENT");
		setValue("time", "10/22/08 6:01 PM"); 
		execute("CRUD.save"); 		
		assertNoErrors(); 
		
		assertValue("number", "");
		assertValue("type", usesAnnotatedPOJO()?"0":"1"); 
		assertValue("mode", usesAnnotatedPOJO()?"0":"1");
		assertValue("description", "");
		assertValue("time", "");
		
		// Search just created
		setValue("type", usesAnnotatedPOJO()?"0":"1");
		setValue("mode", usesAnnotatedPOJO()?"1":"2");
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("type", usesAnnotatedPOJO()?"0":"1"); 		
		assertValue("mode", usesAnnotatedPOJO()?"1":"2");
		assertValue("number", "66");
		assertValue("description", "JUNIT SHIPMENT");
		assertValue("time", "10/22/08 6:01 PM"); 
				
		// Modify
		setValue("description", "JUNIT SHIPMENT MODIFIED");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("type", usesAnnotatedPOJO()?"0":"1");
		assertValue("mode", usesAnnotatedPOJO()?"0":"1");
		assertValue("number", "");		
		assertValue("description", "");
		
		// Verify
		setValue("type", usesAnnotatedPOJO()?"0":"1");
		setValue("mode", usesAnnotatedPOJO()?"1":"2");
		setValue("number", "66");
		execute("CRUD.refresh");		
		assertValue("number", "66");
		assertValue("mode", usesAnnotatedPOJO()?"1":"2");
		assertValue("type", usesAnnotatedPOJO()?"0":"1"); 
		assertValue("description", "JUNIT SHIPMENT MODIFIED");
										
		// Delete
		execute("CRUD.delete");											
		assertNoErrors();
		assertMessage("Shipment deleted successfully");	
	}
	
	public void testDeleteSelectedOnesWithConverterInKey() throws Exception { 
		// Create
		execute("CRUD.new");
		
		setValue("type", usesAnnotatedPOJO()?"0":"1");
		setValue("mode", usesAnnotatedPOJO()?"1":"2");
		setValue("number", "66");
		setValue("description", "JUNIT SHIPMENT");
		execute("CRUD.save"); 		
		assertNoErrors();
		
		execute("Mode.list");
				
		int rowCount = getListRowCount();
		assertTrue("Must to be at least 2 shipments for run this test", rowCount >= 2);
		assertTrue("Must not to be more than 9 shipments for run this test", rowCount < 10);

		boolean found = false;
		int row = 0;
		for (; row < rowCount; row++) {
			String number = getValueInList(row, "number");
			String description = getValueInList(row, "description");
			if ("66".equals(number) && "JUNIT SHIPMENT".equals(description)) {
				found = true;
				break;
			}
		}
		assertTrue("Objet not found in list", found);		
		
		checkRow(row);
				
		execute("CRUD.deleteSelected");
		assertNoErrors();
		
		assertListRowCount(rowCount - 1);
		
		found = false;		
		rowCount = getListRowCount();
		for (row = 0; row < rowCount; row++) {
			String number = getValueInList(row, "number");
			String description = getValueInList(row, "description");
			if ("66".equals(number) && "JUNIT SHIPMENT".equals(description)) {
				found = true;
				break;
			}
		}
		assertTrue("Objet found in list", !found);				
	}
	
	public void testFilterByTimestamp() throws Exception { 
		assertListRowCount(3);
		setConditionValues( new String [] { "", "", "12/25/06"} ); // 2006-12-25
		execute("List.filter");
		assertListRowCount(1); 
		assertValueInList(0, "description", "CINC");
		
		setConditionComparators(new String [] { "=", "=", ">="});
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, "description", "CINC");
		
		setConditionValues( new String [] { "", "", "2006"} );
		setConditionComparators(new String [] { "=", "=", "year_comparator"}); 
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, "description", "CINC");	
		assertValueInList(0, "time", "12/25/06 11:33 AM"); 
		
		setConditionValues( new String [] { "", "", "12/25/06 11:32 AM"} );
		setConditionComparators(new String [] { "=", "=", "="}); 
		execute("List.filter");
		assertListRowCount(0);
		
		setConditionValues( new String [] { "", "", "12/25/06 11:33 AM"} );
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, "description", "CINC");	
		assertValueInList(0, "time", "12/25/06 11:33 AM");
	}
	
	private void deleteCustomerAndContactPerson(int number) throws Exception {
		Customer customer = (Customer) XPersistence.getManager().find(Customer.class, new Integer(number));
		if (customer == null) return;		
		Query query = XPersistence.getManager().createQuery("from CustomerContactPerson as o where o.customer.number = :customerNumber"); 
		query.setParameter("customerNumber", customer.getNumber());  		
		CustomerContactPerson contact = (CustomerContactPerson) query.getSingleResult();
		XPersistence.getManager().remove(contact);
		XPersistence.getManager().remove(customer);
		XPersistence.commit();
	}
			
}
