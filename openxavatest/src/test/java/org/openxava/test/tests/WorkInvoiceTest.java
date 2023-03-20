package org.openxava.test.tests;

import org.openxava.tests.*;


/**
 * 
 * @author Javier Paniza
 */

public class WorkInvoiceTest extends ModuleTestBase {

	
	public WorkInvoiceTest(String testName) {
		super(testName, "WorkInvoice");		
	}
	
	public void testCalculation() throws Exception {
		assertCalculation(".", ","); // TMR FALLA
		setLocale("es");
		assertCalculation(",", ".");
	}

	private void assertCalculation(String decimalSeparator, String groupingSeparator) throws Exception {
		execute("CRUD.new");
		setValue("number", "66");
		setValue("description", "JUNIT WORK INVOICE");
		
		assertValue("vatPercentage", "16");
		
		setValue("hours", "250");
		assertValue("total", "0" + decimalSeparator + "00");
		
		setValue("worker.nickName", "john");
		assertValue("worker.hourPrice", "20" + decimalSeparator + "00");
		assertValue("total", "5" + groupingSeparator + "800" + decimalSeparator + "00"); 
		
		setValue("tripCost", "299" + decimalSeparator + "9");
		assertValue("total", "6" + groupingSeparator + "147" + decimalSeparator + "88");
		
		setValue("discount", "1200");
		assertValue("total", "4" + groupingSeparator + "755" + decimalSeparator + "88");
		
		setValue("vatPercentage", "21");
		assertValue("total", "4" + groupingSeparator + "960" + decimalSeparator + "88");
		
		assertValue("workCost.id", ""); // To assure we save an empty reference used as mappedBy in an @OrderColumn collection, a bug 
		execute("CRUD.save");
		assertNoErrors(); 
		execute("Mode.list");
		setConditionValues("66"); 
		execute("List.filter");
		execute("List.viewDetail", "row=0");
		
		assertValue("number", "66");
		assertValue("hours", "250");
		assertValue("worker.hourPrice", "20" + decimalSeparator + "00");
		assertValue("tripCost", "299" + decimalSeparator + "90");
		assertValue("discount", "1" + groupingSeparator + "200" + decimalSeparator + "00");
		assertValue("vatPercentage", "21");
		assertValue("total", "4" + groupingSeparator + "960" + decimalSeparator + "88");
		
		setValue("vatPercentage", "22");
		assertValue("total", "5" + groupingSeparator + "001" + decimalSeparator + "88"); 
		
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	public void testAssignReferenceMappedByOrderColumnListOnModifyEntity() throws Exception { 
		execute("CRUD.new");
		setValue("number", "66");
		setValue("description", "JUNIT WORK INVOICE");
		execute("CRUD.save");
		
		assertValue("number", "");
		assertValue("description", "");
		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("number", "66");
		assertValue("description", "JUNIT WORK INVOICE");
		
		setValue("workCost.id", "40288118607859d00160786026960000"); 
		execute("CRUD.save");
		assertNoErrors();
		
		changeModule("WorkCost");
		execute("List.viewDetail", "row=0");
		assertValue("description", "CAR SERVICE");
		assertCollectionRowCount("invoices", 1);
		assertValueInCollection("invoices", 0, 0, "66");
		assertValueInCollection("invoices", 0, 1, "JUNIT WORK INVOICE");
		
		changeModule("WorkInvoice");

		setValue("number", "66");
		execute("CRUD.refresh");
		assertValue("number", "66");
		assertValue("description", "JUNIT WORK INVOICE");
		execute("CRUD.delete");
		assertNoErrors();
	}
			
}
