package org.openxava.test.tests;

import java.util.*;

import org.openxava.test.model.*;
import org.openxava.tests.*;
import org.openxava.util.*;


/**
 *  
 * @author Javier Paniza
 */

public class OnlyReadDetailsInvoiceTest extends ModuleTestBase {
	
	public OnlyReadDetailsInvoiceTest(String testName) {
		super(testName, "OnlyReadDetailsInvoice");		
	}
	
	public void testAggregatesCollectionReadOnly() throws Exception { 
		execute("CRUD.new");
		String [] initActions = {
			"Navigation.previous",
			"Navigation.first",
			"Navigation.next",
			"CRUD.new",
			"CRUD.save",
			"CRUD.refresh",
			"Mode.list",
			"List.filter", 
			"List.orderBy",
			"List.changeColumnName", 
			"List.sumColumn", 
			"Print.generatePdf", // the DefaultListActionsForCollections (as ListActions) 
			"Print.generateExcel" // are alwasy present						
		};		
		assertActions(initActions); 

		Invoice invoice = getInvoice();
		setValue("year", String.valueOf(invoice.getYear()));
		setValue("number", String.valueOf(invoice.getNumber()));
		execute("CRUD.refresh");
		assertNoErrors();

		String [] aggregatListActions = {
			"Navigation.previous",
			"Navigation.first",
			"Navigation.next",
			"CRUD.new",
			"CRUD.save",
			"CRUD.delete",
			"CRUD.refresh",
			"Mode.list",
			"Collection.view",
			"List.filter", 
			"List.orderBy", 
			"List.sumColumn",
			"List.changeColumnName", 
			"Print.generatePdf", // the DefaultListActionsForCollections (as ListActions) 
			"Print.generateExcel" // are always present						
		};		
		assertActions(aggregatListActions);
		
		execute("Collection.view", "row=0,viewObject=xava_view_details");
		
		String [] aggregateDetailActions = {
			"Gallery.edit",
			"Collection.hideDetail",
		};		
		assertActions(aggregateDetailActions);
		
		assertNoEditable("serviceType");		
		assertNoEditable("product.number");
		assertNoEditable("product.description");
	}
	
	public void testLevel4ReferenceInList() throws Exception {
		// Look for a invoice with seller level
		int c = getListRowCount();
		int row = -1;
		for (int i=0; i<c; i++) {
			String sellerLevel = getValueInList(i, "customer.seller.level.description");			
			if (!Is.emptyString(sellerLevel)) {
				row = i;
				break;
			}
		}
		assertTrue("It's required a invoice with seller level for run this test", row >= 0); 
		String year = getValueInList(row, "year");
		String number = getValueInList(row, "number");
		Invoice invoice = (Invoice) Invoice.findByYearNumber(Integer.parseInt(year), Integer.parseInt(number));		
		assertValueInList(row, "customer.seller.level.description", invoice.getCustomer().getSeller().getLevel().getDescription());		
	}

	private Invoice getInvoice() throws Exception {
		Iterator it = Invoice.findAll().iterator();
		while (it.hasNext()) {			
			Invoice invoice = (Invoice) it.next();
			if (invoice.getDetailsCount() > 0) {
				return invoice; 
			}			
		}
		fail("It must to exist at least one invoice with details for run this test");
		return null;
	}
	
	
								
}
