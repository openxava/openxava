package org.openxava.test.tests;

import javax.persistence.*;

import org.openxava.jpa.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class DeliveryTypeWithDeliveriesTest extends ModuleTestBase {
	
	public DeliveryTypeWithDeliveriesTest(String testName) {
		super(testName, "DeliveryTypeWithDeliveries");		
	}
	
	public void testNestedCollectionsWhereParentCollectionIsNotCascade() throws Exception { 
		execute("CRUD.new");
		setValue("number", "66");
		setValue("description", "JUNIT DELIVERY TYPE");
		execute("Collection.new", "viewObject=xava_view_deliveries");
		setValue("invoice.year", "2002");
		setValue("invoice.number", "1");
		setValue("number", "77");
		assertExists("date");
		execute("Collection.new", "viewObject=xava_view_details");
		assertDialogTitle("Create a new entity - Delivery detail"); // Not to test the title format but to test that we are in the correct dialog
		assertNotExists("date");
		setValue("number", "88");
		setValue("description", "JUNIT DELIVERY DETAIL");		
		execute("Collection.save");
		assertNoErrors();
		assertMessage("Delivery type created successfully");
		assertMessage("Delivery created successfully"); 
		assertMessage("Delivery detail created successfully");
		execute("Collection.hideDetail");
		
		execute("Mode.list");
		assertValueInList(6, 0, "66");
		execute("List.viewDetail", "row=6");
		assertValue("number", "66");
		assertCollectionRowCount("deliveries", 1);
		execute("Collection.edit", "row=0,viewObject=xava_view_deliveries");
		assertValue("number", "77");
		assertCollectionRowCount("details", 1);
		assertValueInCollection("details", 0, 0, "88");
		assertValueInCollection("details", 0, 1, "JUNIT DELIVERY DETAIL");
		
		delete("DeliveryDetail", "88");
		delete("Delivery", "77");
		delete("DeliveryType", "66");
	}
	
	private void delete(String entity, String number) {
		Query query = XPersistence.getManager().createQuery("delete from " + entity + " e where e.number = " + number);
		query.executeUpdate();		
	}
			
}
