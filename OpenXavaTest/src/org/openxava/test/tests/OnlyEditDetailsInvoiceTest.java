package org.openxava.test.tests;

import java.util.*;
import javax.rmi.*;

import org.openxava.test.model.*;
import org.openxava.tests.*;



/**
 * 
 * @author Javier Paniza
 */

public class OnlyEditDetailsInvoiceTest extends ModuleTestBase {
	
	public OnlyEditDetailsInvoiceTest(String testName) {
		super(testName, "OnlyEditDetailsInvoice");		
	}
	
	public void testAggregatesCollectionEditOnly() throws Exception { 
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

		String [] aggregateListActions = {
			"Navigation.previous",
			"Navigation.first",
			"Navigation.next",
			"CRUD.new",
			"CRUD.save",
			"CRUD.delete",
			"CRUD.refresh",
			"Mode.list",
			"Collection.edit",
			"List.filter", 
			"List.orderBy", 
			"List.sumColumn",
			"List.changeColumnName", 
			"Print.generatePdf", // the DefaultListActionsForCollections (as ListActions) 
			"Print.generateExcel" // are alwasy present			
		};		
		assertActions(aggregateListActions);
		
		execute("Collection.edit", "row=0,viewObject=xava_view_details");
		
		String [] aggregateDetailActions = {
			"Reference.search",
			"Reference.createNew",
			"Reference.modify",
			"Gallery.edit",
			"Collection.save",
			"Collection.hideDetail",
		};		
		assertActions(aggregateDetailActions);
		
		assertEditable("serviceType");						
	}

	private Invoice getInvoice() throws Exception {
		Iterator it = Invoice.findAll().iterator();
		while (it.hasNext()) {			
			Invoice invoice = (Invoice) it.next();  
			if (invoice.getDetailsCount() > 0) {
				return invoice; 
			}			
		}
		fail("It must to exist at least one invoice with details to run this test");
		return null;
	}
	
	
								
}
