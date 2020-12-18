package org.openxava.test.tests;

/**
 * @author Javier Paniza
 */

public class TransportChargeWithDescriptionsListShowingReferenceViewTest extends TransportChargeTestBase {
	
	public TransportChargeWithDescriptionsListShowingReferenceViewTest(String testName) {
		super(testName, "TransportChargeWithDescriptionsListShowingReferenceView");		
	}
	
	public void testCompositeKeyDescriptionsListWithShowReferenceView() throws Exception { 
		deleteAll();
		createSome();
		resetModule(); // For refresh 
		assertValueInList(1, 0, "2004"); 
		execute("List.viewDetail", "row=1");
		assertNoAction("Reference.createNew", "model=Delivery,keyProperty=delivery__KEY__");
		assertNoAction("Reference.modify", "model=Delivery,keyProperty=delivery__KEY__");
		assertNoEditable("delivery"); 
		assertNoEditable("delivery.invoice.year");
		assertNoEditable("delivery.type");
		assertNoEditable("delivery.date");
		assertEditable("amount");
		assertValue("delivery.KEY", "[.2.2004.666.1.]");
		assertDescriptionValue("delivery.KEY", "DELIVERY JUNIT 666 2/22/04");
		assertValue("delivery.invoice.year", "2004");
		assertValue("delivery.type.number", "1");
		assertValue("delivery.date", "2/22/04");
		
		execute("CRUD.new");
		assertAction("Reference.createNew", "model=Delivery,keyProperty=delivery__KEY__");
		assertAction("Reference.modify", "model=Delivery,keyProperty=delivery__KEY__");
		assertEditable("delivery");
		assertNoEditable("delivery.invoice.year");
		assertNoEditable("delivery.type");
		assertNoEditable("delivery.date");
		assertEditable("amount");
		assertValue("delivery.KEY", "");
		assertDescriptionValue("delivery.KEY", "");
		assertValue("delivery.invoice.year", "");
		assertValue("delivery.type.number", "");
		assertValue("delivery.date", "");
		
		setValue("delivery.KEY", "[.2.2004.777.2.]");
		assertValue("delivery.KEY", "[.2.2004.777.2.]");
		assertValue("delivery.invoice.year", "2004");
		assertValue("delivery.type.number", "2");
		assertValue("delivery.date", "6/23/06");
		assertValue("delivery.description", "FOR TEST SEARCHING BY DESCRIPTION");
		
		setValue("amount", "799");
		execute("CRUD.save");
		execute("Mode.list");
		assertValueInList(2, 0, "2004");
		assertValueInList(2, 3, "799.00");
		execute("List.viewDetail", "row=2");
		assertValue("delivery.KEY", "[.2.2004.777.2.]");
		assertDescriptionValue("delivery.KEY", "FOR TEST SEARCHING BY DESCRIPTION 6/23/06");
		assertValue("delivery.invoice.year", "2004");
		assertValue("delivery.invoice.date", "1/4/04"); 
		assertValue("delivery.type.number", "2");
		assertValue("delivery.date", "6/23/06");
		assertValue("amount", "799.00");
		assertValue("delivery.description", "FOR TEST SEARCHING BY DESCRIPTION");
		
		execute("Sections.change", "activeSection=1"); // To test a bug that clear the not key members 
		assertValue("delivery.invoice.date", "1/4/04"); // on action call
		
		execute("CRUD.delete");
		assertNoErrors();
	}
		
}
