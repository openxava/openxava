package com.yourcompany.yourapp.tests;

import org.openxava.tests.*;

public class MasterTest extends ModuleTestBase {
	
	public MasterTest(String testName) {
		super(testName, "Master");
	}
	
	public void testCreateReadUpdateDelete() throws Exception {
		login("admin", "admin");
		// Create
		execute("CRUD.new");
		setValue("year", "2026");
		setValue("number", "99999");
		setValue("person.number", "1"); // Wim Mertens
		
		// Add first detail
		assertCollectionRowCount("details", 0);
		setValueInCollection("details", 0, "item.number", "1"); // Learn OpenXava by example
		assertValueInCollection("details", 0, "unitPrice", "19.00");
		setValueInCollection("details", 0, "quantity", "5");
		assertValueInCollection("details", 0, "amount", "95.00");
		
		// Add second detail
		setValueInCollection("details", 1, "item.number", "2"); // Aprende OpenXava con ejemplos
		assertValueInCollection("details", 1, "unitPrice", "19.00");
		setValueInCollection("details", 1, "quantity", "3");
		assertValueInCollection("details", 1, "amount", "57.00");
		
		assertCollectionRowCount("details", 2);
		
		// Verify totals: sum=152.00, vatPercentage=21%, vat=31.92, total=183.92
		assertTotalInCollection("details", 0, "amount", "152.00");
		assertTotalInCollection("details", 1, "amount", "21");
		assertTotalInCollection("details", 2, "amount", "31.92");
		assertTotalInCollection("details", 3, "amount", "183.92");
		
		execute("CRUD.save");
		assertNoErrors();
		assertMessage("Master created successfully");
		
		// Read - search for the created master
		execute("Mode.list");
		execute("CRUD.new");
		setValue("year", "2026");
		setValue("number", "99999");
		execute("CRUD.refresh");
		assertNoErrors();
		assertValue("year", "2026");
		assertValue("number", "99999");
		assertValue("person.number", "1");
		assertCollectionRowCount("details", 2);
		assertValueInCollection("details", 0, "quantity", "5");
		assertValueInCollection("details", 1, "quantity", "3");
		
		// Update - modify quantity of first detail
		setValueInCollection("details", 0, "quantity", "10");
		assertValueInCollection("details", 0, "amount", "190.00");
		
		// Verify updated totals: sum=247.00, vatPercentage=21%, vat=51.87, total=298.87
		assertTotalInCollection("details", 0, "amount", "247.00");
		assertTotalInCollection("details", 1, "amount", "21");
		assertTotalInCollection("details", 2, "amount", "51.87");
		assertTotalInCollection("details", 3, "amount", "298.87");
		
		execute("CRUD.save");
		assertNoErrors();
		assertMessage("Master modified successfully");
		
		// Verify the modification using list
		execute("Mode.list");
        setConditionValues("2026", "99999");
        execute("List.filter");

		execute("List.viewDetail", "row=0");
		assertValue("year", "2026");
		assertValue("number", "99999");
		assertCollectionRowCount("details", 2);
		assertValueInCollection("details", 0, "quantity", "10");
		assertValueInCollection("details", 1, "quantity", "3");
		
		// Verify totals are persisted correctly
		assertTotalInCollection("details", 0, "amount", "247.00");
		assertTotalInCollection("details", 1, "amount", "21");
		assertTotalInCollection("details", 2, "amount", "51.87");
		assertTotalInCollection("details", 3, "amount", "298.87");
		
		// Delete
		execute("CRUD.delete");
		assertMessage("Master deleted successfully");
	}	
	
	public void testPrint() throws Exception{
		login("admin", "admin");
		execute("List.viewDetail", "row=0");
		execute("Master.print");
		assertContentTypeForPopup("application/pdf");
		assertPopupPDFLine( 0, "MASTER");
		assertPopupPDFLine( 1, "Master: 2020/1");
		assertPopupPDFLine( 2, "Date: 8/13/2020");
		assertPopupPDFLine( 3, "Person:");
		assertPopupPDFLine( 4, "Number: 5");
		assertPopupPDFLine( 5, "Bill Gates");
		assertPopupPDFLine( 6, "Avenue Mortheast");
		assertPopupPDFLine( 7, "Medina");
		assertPopupPDFLine( 8, "United States");
		assertPopupPDFLine( 9, "Number Description Quantity Unit Price Amount");
		assertPopupPDFLine(10, "1 Learn OpenXava by example 3 19.00 57.00");
		assertPopupPDFLine(11, "3 XavaPro Professional 10 499.00 4,990.00");
		assertPopupPDFLine(12, "TAX Rate: 21%");
		assertPopupPDFLine(13, "TAX: 1,059.87");
		assertPopupPDFLine(14, "TOTAL: 6,106.87");	
	}
	
}
