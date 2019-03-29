package org.openxava.test.tests;

import org.openxava.jpa.*;
import org.openxava.tests.*;
import org.openxava.util.*;


/**
 * 
 * @author Javier Paniza
 */

public class TransportCharge2Test extends ModuleTestBase {
			
	public TransportCharge2Test(String testName) {
		super(testName, "TransportCharge2");		
	}
	
	public void testKeyNestedOverlappedReferences() throws Exception {
		deleteAll();
		
		execute("CRUD.new");
		setValue("year", "2002");
		execute("Reference.search", "keyProperty=xava.TransportCharge2.delivery.number");
		String year = getValueInList(0, 0);
		String number = getValueInList(0, 1);
		String description = getValueInList(0, 6);
		assertTrue("It is required that year in invoice has value", !Is.emptyString(year));
		assertTrue("It is required that number in invoice has value", !Is.emptyString(number));
		
		execute("ReferenceSearch.choose", "row=0");
		assertNoErrors();
		assertValue("delivery.invoice.year", year); 
		assertValue("delivery.invoice.number", number);
		assertValue("delivery.description", description); // To test if data from reference is loaded
		
		setValue("amount", "666");
		execute("CRUD.save");
		assertNoErrors();
				
		assertValue("year", "");
		assertValue("delivery.invoice.year", "");
		assertValue("delivery.invoice.number", "");
		assertValue("amount", "");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		assertValue("year", "2002"); 
		assertValue("delivery.invoice.year", year);
		assertValue("delivery.invoice.number", number);
		assertValue("amount", "666.00");
		
		setValue("amount", "777");
		execute("CRUD.save");
		assertNoErrors(); 

		assertValue("year", "");
		assertValue("delivery.invoice.year", "");
		assertValue("delivery.invoice.number", "");
		assertValue("amount", "");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertNoErrors();
		assertValue("year", "2002");
		assertValue("delivery.invoice.year", year);
		assertValue("delivery.invoice.number", number);
		assertValue("amount", "777.00");
						
		execute("CRUD.delete");										
		assertMessage("Transport charge 2 deleted successfully");
	}
	
	private void deleteAll() throws Exception {
		XPersistence.getManager().createQuery("delete from TransportCharge2").executeUpdate();
		XPersistence.commit(); 		
	}	
	
}
