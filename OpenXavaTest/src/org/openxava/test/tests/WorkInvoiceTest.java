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
		assertCalculation(".", ","); 
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
		assertValue("total", "5800" + decimalSeparator + "00");
		
		setValue("tripCost", "299" + decimalSeparator + "9");
		assertValue("total", "6147" + decimalSeparator + "88");
		
		setValue("discount", "1200");
		assertValue("total", "4755" + decimalSeparator + "88");
		
		setValue("vatPercentage", "21");
		assertValue("total", "4960" + decimalSeparator + "88");
		
		execute("CRUD.save");
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
		assertValue("total", "5001" + decimalSeparator + "88");
		
		execute("CRUD.delete");
		assertNoErrors();
	}
		
}
