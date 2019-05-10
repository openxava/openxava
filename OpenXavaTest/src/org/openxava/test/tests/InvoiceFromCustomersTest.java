package org.openxava.test.tests;

import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class InvoiceFromCustomersTest extends ModuleTestBase {
	
		
	private Collection invoices;

	public InvoiceFromCustomersTest(String testName) {
		super(testName, "InvoiceFromCustomers");		
	}
	
	public void testGenerateExcelInASecondModule_baseConditionAndDefaultOrderExcludedFromListConfigurationName() throws Exception{ 
		execute("Invoice.listOfCustomer", "row=0");
		assertListSelectedConfiguration("All");  
		setConditionValues("2011");
		execute("List.filter");
		assertListSelectedConfiguration("Year = 2011");
		assertListRowCount(1);
		execute("Print.generateExcel");
		assertAction("Print.generateExcel");
	}
	
	public void testSelectedFromCalledModuleNotPropagateToCallerModule() throws Exception { 
		assertRowUnchecked(0);
		execute("Invoice.listOfCustomer", "row=0");
		assertListNotEmpty();
		checkRow(0);
		assertRowChecked(0);
		execute("CustomerInvoices.return");
		assertRowUnchecked(0); 
	}
	
	public void testListActionsIncludedWhenModeControllerUsed() throws Exception {
		executeDefaultAction();
		assertNoPopup();		
	}
	
	public void testModuleChange() throws Exception { 
		// Verifying in initial module
		assertAction("Invoice.listOfCustomer");
		assertNoAction("CustomerInvoices.return");
		assertValueInList(0, 0, "Javi");
		assertMessage("Action on-each-request on list");
		assertMessage("Action on-each-request");
				
		// Go to another module
		execute("Invoice.listOfCustomer", "row=0");
		assertNoAction("Invoice.listOfCustomer"); 
		assertAction("CustomerInvoices.return");				
		assertInvoices(); 
		// The next two lines because the last action was from the initial module
		assertMessage("Action on-each-request on list");  
		assertMessage("Action on-each-request");	
		execute("List.viewDetail", "row=0");
		assertNoMessages();
		assertNoErrors();
		setModel("Invoice");
		assertExists("year");
		setModelToModuleSetting();
		execute("Mode.list");
		assertInvoices();
				
		// Return to initial module
		execute("CustomerInvoices.return");
		assertAction("Invoice.listOfCustomer");
		assertNoAction("CustomerInvoices.return");
		assertValueInList(0, 0, "Javi");		
				
		// Go again to the other module
		execute("Invoice.listOfCustomer", "row=0");
		assertNoAction("Invoice.listOfCustomer");
		assertAction("CustomerInvoices.return");
		assertInvoices();
		execute("List.viewDetail", "row=0"); // to detail
		
		// Return to initial module
		execute("CustomerInvoices.return");
		assertAction("Invoice.listOfCustomer"); 
		assertNoAction("CustomerInvoices.return");
		assertValueInList(0, 0, "Javi");
		
		// Go again to the other module, now we should to entry in list, not in detail
		execute("Invoice.listOfCustomer", "row=0");
		assertNoAction("Invoice.listOfCustomer");
		assertAction("CustomerInvoices.return");
		assertInvoices();		
		
		// Return to initial module using a chained action
		execute("CustomerInvoices.returnWithChainedAction");
		assertAction("Invoice.listOfCustomer");
		assertNoAction("CustomerInvoices.return");
		assertValueInList(0, 0, "Javi");		
	}
	
	public void testSetBaseConditionOnChangeModule() throws Exception { 
		execute("Invoice.listOfCustomer", "row=0");
		assertInvoices("1", 5); 
		execute("CustomerInvoices.returnWithChainedAction");
		
		execute("Invoice.listOfCustomer", "row=1");
		assertInvoices("2", 2); 
		
		setConditionValues("", "", "", "2");
		execute("List.filter");
		assertInvoices("2", 2);
		
		execute("CustomerInvoices.returnWithChainedAction");
		execute("Invoice.listOfCustomer", "row=0");
		assertInvoices("1", 5);		
		
		assertListSelectedConfiguration("All"); 
	}
	
	private void assertInvoices(String customerNumber, int invoicesCount) throws Exception { 
		assertListRowCount(invoicesCount);
		for (int i = 0; i < invoicesCount; i++) {
			assertValueInList(i, 3, customerNumber);
		}	
		
	}
	
	private void assertInvoices() throws Exception {
		assertListRowCount(getInvoices().size());		
		Iterator it = getInvoices().iterator();
		while (it.hasNext()) {
			Invoice invoice = (Invoice) it.next();
			assertInvoiceInList(invoice);			
		}
	}

	private void assertInvoiceInList(Invoice invoice) throws Exception {
		int rowCount = getListRowCount();
		String year = String.valueOf(invoice.getYear());
		String number = String.valueOf(invoice.getNumber());
		for (int i = 0; i < rowCount; i++) {
			if (year.equals(getValueInList(i, 0)) && number.equals(getValueInList(i, 1))) return;
		}	
		fail("Invoice " + year + "/" + number + " deber�a estar presente en la lista");
	}

	private Collection getInvoices() throws Exception {
		if (invoices == null) {
			Query query = XPersistence.getManager().createQuery("select i from Invoice as i where i.customer.number=1" );	
			invoices = query.getResultList();    // Javi   
		} 
		return invoices;		
	}
		
}
