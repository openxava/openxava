package org.openxava.test.tests.bymodule;

import static org.openxava.jpa.XPersistence.getManager;

import java.util.*;

import javax.persistence.*;

import org.htmlunit.html.*;
import org.openxava.tests.*;
import org.openxava.util.*;


/**
 * @author Javier Paniza
 */

public class OrderTest extends ModuleTestBase {	
	
	public OrderTest(String testName) {
		super(testName, "Order");		
	}
	
	public void testGoodPerformanceWithCalculatedPropertiesEnteringInCalendar() throws Exception { 
		execute("ListFormat.select", "editor=Calendar");
		long ini = System.currentTimeMillis();
		resetModule();
		long takes = System.currentTimeMillis() - ini;
		assertTrue(takes < 4000); // With the fix it takes over 1200, without it it taken around 7000 (but never less 5600)
		
		// In the case of Charts we reduced the time at twice, but it still works slowly with calculated properties, something to improved
	}
	
	public void testGoodPerformanceWithCalculatedPropertiesInList_actionsNotLostAfterOpenDialogTwiceFromCollectionElement() throws Exception { 
		long ini = System.currentTimeMillis();
		execute("List.filter");
		long takes = System.currentTimeMillis() - ini; 
		assertTrue(takes < 4000); // With the fix it takes over 2500, without it it taken around 8000 (but never less 5600)
		
		execute("Print.generateExcel");
		assertContentTypeForPopup("text/x-csv");		
		StringTokenizer excel = new StringTokenizer(getPopupText(), "\n\r");
		excel.nextToken();
		String line1 = excel.nextToken();
		assertEquals("2017;1;\"5/10/2017\";1;\"Javi\";\"\";\"No\";\"110.00\"", line1);
		
		execute("ListFormat.select", "editor=Cards");
		assertListRowCount(56);
		resetModule();
		assertListRowCount(56);
		
		ini = System.currentTimeMillis(); 
		execute("ListFormat.select", "editor=List");
		takes = System.currentTimeMillis() - ini; 
		assertTrue(takes < 4000); // With the fix it takes 2500, without it it taken around 8000 (but never less 5600)
		
		setLocale("zh");
		assertLabelInList(3,"客户编号"); // TMR FALLA. EL ÚNICO DE TODA LA SUITE
		setLocale("en");
		
		execute("List.viewDetail", "row=0"); 
		execute("Collection.new", "viewObject=xava_view_details"); 	

		assertAction("Collection.save");
		assertNoAction("ReferenceSearch.choose");		
		execute("Reference.search", "keyProperty=product.number");
		assertNoAction("Collection.save");
		assertAction("ReferenceSearch.choose");				
		execute("ReferenceSearch.choose", "row=0");

		assertAction("Collection.save");
		assertNoAction("ReferenceSearch.choose");		
		execute("Reference.search", "keyProperty=product.number");
		assertNoAction("Collection.save");
		assertAction("ReferenceSearch.choose");				
		execute("ReferenceSearch.choose", "row=0");

		assertAction("Collection.save");
		assertNoAction("ReferenceSearch.choose");		
	}
	
	public void testRemoveActionTwice() throws Exception{
		execute("CRUD.new");
		assertNoAction("Order.onlyButton");
		execute("Reference.search", "keyProperty=customer.number");
		execute("ReferenceSearch.choose", "row=0");
		execute("CRUD.save");
		assertNoErrors(); 
		execute("CRUD.refresh");
		execute("CRUD.delete");
	}
	
	public void testCalculatedPropertiesFromCollection_generatedValueOnPersistRefreshedInView_rowAction_noAddActionInCascadeCollections_idInCreationMessageWhenEmptySearchKeys() throws Exception {
		setLocale("es"); // Verify that entity names are translated in the messages  
		String nextNumber = getNextNumber();
		execute("CRUD.new");
		assertValue("number", ""); 
		setValue("customer.number", "1");
		assertValue("customer.name", "Javi");
		assertNoAction("Collection.add"); 
		assertCollectionRowCount("details", 0); 
		execute("Collection.new", "viewObject=xava_view_details");
		setValue("product.number", "1"); 
		assertValue("product.description", "MULTAS DE TRAFICO");
		assertValue("product.unitPrice", "11,00");
		setValue("quantity", "10");
		assertValue("amount", "110,00"); 
		execute("Collection.save");
		assertNoErrors(); 
		assertMessage("Orden creado/a satisfactoriamente"); 
		assertCollectionRowCount("details", 1);
		assertValue("amount", "110,00");
		assertValue("number", nextNumber);
		assertValueInCollection("details", 0, "quantity", "10");
		assertValueInCollection("details", 0, "amount", "110,00");
		execute("OrderDetail.reduceQuantity", "row=0,viewObject=xava_view_details");
		assertValueInCollection("details", 0, "quantity", "9");
		assertValueInCollection("details", 0, "amount", "99,00");
		assertNoAction("OrderDetail.reduceQuantity", "viewObject=xava_view_details");
		execute("CRUD.delete");
		assertNoErrors();
		
		// Id in creation message when empty search keys
		execute("CRUD.new");
		assertValue("number", ""); 
		setValue("customer.number", "1");
		assertValue("customer.name", "Javi");
		String year = getValue("year");
		execute("CRUD.save");
		assertMessage("Orden creado/a satisfactoriamente: " + year  + "/" + nextNumber); 
		
		execute("Mode.list");
		setConditionValues(year, nextNumber);
		execute("List.filter");
		assertValueInList(0, 0, year);
		assertValueInList(0, 1, nextNumber);
		execute("CRUD.deleteRow", "row=0");
		assertNoErrors();
	}
	
	
	public void testDoubleClickOnlyInsertsACollectionElement() throws Exception {
		boolean doubleClick = false; 
		while (!doubleClick) { 
			execute("CRUD.new");
			setValue("customer.number", "1"); 
			assertCollectionRowCount("details", 0); 
			execute("Collection.new", "viewObject=xava_view_details");
			setValue("product.number", "1");
			setValue("quantity", "10");
			HtmlElement action = getHtmlPage().getHtmlElementById(decorateId("Collection.save"));
					
			action.click(); // Not dblClick(), it does not reproduce the problem
			try {
				getHtmlPage().getHtmlElementById(decorateId("Collection.save"));
			}
			catch (org.htmlunit.ElementNotFoundException ex) {
				continue; // Because sometimes the action is executed very fast and 
					// when the second click happens the dialog is already closed
				    // This case cannot occurs in real life (because with no dialog 
					// there is no button to click) but it can occurs in test 
					// (because we have a reference to the link) 
			}
			action.click();
			doubleClick = true;
			Thread.sleep(4000);
					
			assertNoErrors(); 
			assertCollectionRowCount("details", 1);
			
			execute("CRUD.delete");
			assertNoErrors();
		}
	}
	
	private String getNextNumber() throws Exception {
		Query query = getManager().
			createQuery(
				"select max(o.number) from Order o where o.year = :year"); 
		query.setParameter("year", Dates.getYear(new Date()));
		Integer lastNumber = (Integer) query.getSingleResult();
		if (lastNumber == null) lastNumber = 0;
		return Integer.toString(lastNumber + 1);
	}

								
}
