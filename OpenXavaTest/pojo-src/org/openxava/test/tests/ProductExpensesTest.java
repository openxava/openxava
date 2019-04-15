package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 *  
 * @author Javier Paniza
 */

public class ProductExpensesTest extends ModuleTestBase {
	
	public ProductExpensesTest(String testName) {
		super(testName, "ProductExpenses");		
	}
		
	public void testDescriptionsListInElementCollection() throws Exception {
		execute("CRUD.new");
		setValue("description", "JUNIT EXPENSES");
		
		String [][] invoiceValidValues = {
			{ "", "" },
			{ "[.1.2002.]", "2002 1" },
			{ "[.10.2004.]", "2004 10" },
			{ "[.11.2004.]", "2004 11" },
			{ "[.12.2004.]", "2004 12" },
			{ "[.2.2004.]", "2004 2" },
			{ "[.9.2004.]", "2004 9" },
			{ "[.14.2007.]", "2007 14" },
			{ "[.1.2009.]", "2009 1" },
			{ "[.1.2011.]", "2011 1" }			
		};		
		assertValidValuesInCollection("expenses", 0, "invoice.KEY", invoiceValidValues); 
		String [][] productValidValues = {
			{ "", "" },
			{ "4", "CUATRE" },
			{ "2", "IBM ESERVER ISERIES 270" },
			{ "1", "MULTAS DE TRAFICO" },
			{ "5", "PROVAX" },
			{ "6", "SEIS" },
			{ "7", "SIETE" },
			{ "3", "XAVA" }
		};		
		assertValidValuesInCollection("expenses", 0, "product.number", productValidValues);

		String [][] carrierValidValues = {
			{ "", "" },
			{ "4", "CUATRO" },
			{ "5", "Cinco" },
			{ "2", "DOS" },
			{ "3", "TRES" },
			{ "1", "UNO" }
		};		
		assertValidValuesInCollection("expenses", 0, "carrier.number", carrierValidValues); 
		
		String [][] familyValidValues = {
			{ "", "" },
			{ "2", "HARDWARE" },
			{ "3", "SERVICIOS" },
			{ "1", "SOFTWARE" }
		};		
		assertValidValuesInCollection("expenses", 0, "family.number", familyValidValues);
		String [][] noValidValues = {
			{ "", "" }	
		};
		assertValidValuesInCollection("expenses", 0, "subfamily.number", noValidValues);
		
		assertValueInCollection("expenses", 0, "carrier.number", ""); 
		setValueInCollection("expenses", 0, "invoice.KEY", "[.1.2002.]");
		assertValueInCollection("expenses", 0, "carrier.number", "3"); 
		setValueInCollection("expenses", 0, "product.number", "4");
		assertValidValuesInCollection("expenses", 0, "subfamily.number", noValidValues);
		setValueInCollection("expenses", 0, "family.number", "1");
		String [][] subfamily1ValidValues = {
			{ "", "" },
			{ "1", "DESARROLLO" },
			{ "2", "GESTION" },
			{ "3", "SISTEMA" }
		};		
		assertValidValuesInCollection("expenses", 0, "subfamily.number", subfamily1ValidValues); 
		setValueInCollection("expenses", 0, "subfamily.number", "3");
						
		execute("CRUD.save");
		
		assertValue("description", "");
		assertValueInCollection("expenses", 0, "invoice.KEY", "");
		assertValueInCollection("expenses", 0, "product.number", ""); 
		assertValueInCollection("expenses", 0, "carrier.number", "");
		assertValueInCollection("expenses", 0, "family.number", ""); 
		assertValueInCollection("expenses", 0, "subfamily.number", ""); 
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");

		assertValue("description", "JUNIT EXPENSES");
		assertValueInCollection("expenses", 0, "invoice.KEY", "[.1.2002.]");
		assertValueInCollection("expenses", 0, "product.number", "4"); 
		assertValueInCollection("expenses", 0, "carrier.number", "3");
		assertValueInCollection("expenses", 0, "family.number", "1"); 
		assertValueInCollection("expenses", 0, "subfamily.number", "3"); 

		execute("CRUD.delete");
		assertNoErrors();
	}
	
	
	public void testChoosingInADescriptionsListComboAddsLineInAnElementCollection() throws Exception {   
		execute("CRUD.new");
		HtmlElement editor = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_ProductExpenses__reference_editor_expenses___0___invoice");
		HtmlElement handler = editor.getElementsByTagName("i").get(0);
		assertCollectionRowCount("expenses", 0); 
		handler.click();
		HtmlElement combo = getHtmlPage().getHtmlElementById("ui-id-1");
		((HtmlElement) combo.getFirstElementChild()).click();
		assertCollectionRowCount("expenses", 1);
	}
			
}
