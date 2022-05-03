package org.openxava.test.tests;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 *  
 * @author Javier Paniza
 */

public class ProductExpenses2Test extends ModuleTestBase {
	
	public ProductExpenses2Test(String testName) {
		super(testName, "ProductExpenses2");		
	}
		
	public void testDescriptionsListWithDefaultValueInElementCollection_descriptionsListAfterRemovingRowInElementCollection() throws Exception {  
		getWebClient().getOptions().setCssEnabled(true); 
		execute("CRUD.new");
		setValue("description", "JUNIT EXPENSES");
		
		assertValueInCollection("expenses", 0, "invoice.KEY", "");
		assertValueInCollection("expenses", 0, "product.number", ""); 
		setValueInCollection("expenses", 0, "carrier.number", "3");  
		
		assertValueInCollection("expenses", 0, "invoice.KEY", "[.1.2002.]");
		assertComboDescription("invoice__KEY__", "2002 1"); 
		assertValueInCollection("expenses", 0, "product.number", "2");	
		assertComboDescription("product___number", "IBM ESERVER ISERIES 270"); 
		
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("description", "JUNIT EXPENSES");
		assertCollectionRowCount("expenses", 1);
		assertValueInCollection("expenses", 0, "carrier.number", "3");  
		assertValueInCollection("expenses", 0, "invoice.KEY", "[.1.2002.]"); 
		assertValueInCollection("expenses", 0, "product.number", "2");	
		
		setValueInCollection("expenses", 1, "carrier.number", "2");
		setValueInCollection("expenses", 2, "carrier.number", "1");
		assertOpenCombo(0);
		assertOpenCombo(1);
		assertOpenCombo(2);
		
		removeRow(1);
		assertOpenCombo(0);
		assertOpenCombo(1);
		
		execute("CRUD.delete");
		assertNoErrors();
	}

	private void assertComboDescription(String member, String expectedDescription) {
		HtmlInput input = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_ProductExpenses2__expenses___0___" + member);
		HtmlInput control = (HtmlInput) input.getPreviousElementSibling();
		assertEquals(expectedDescription, control.getValueAttribute());
		HtmlInput description = (HtmlInput) input.getNextElementSibling();
		assertEquals(expectedDescription, description.getValueAttribute());
	}
	
	private void assertOpenCombo(int row) throws Exception {
		HtmlElement comboEditor = getHtmlPage().getHtmlElementById(
			"ox_OpenXavaTest_ProductExpenses2__reference_editor_expenses___" + row + "___product");		
	    HtmlElement iconDown = comboEditor.getElementsByAttribute("i", "class", "mdi mdi-menu-down").get(0);
	    HtmlElement iconUp = comboEditor.getElementsByAttribute("i", "class", "mdi mdi-menu-up").get(0);
	    assertTrue(iconDown.isDisplayed());
	    assertFalse(iconUp.isDisplayed());
	    iconDown.click();
	    assertFalse(iconDown.isDisplayed());
	    assertTrue(iconUp.isDisplayed());	    
	    iconUp.click();
	    assertTrue(iconDown.isDisplayed());
	    assertFalse(iconUp.isDisplayed());
	}
		
	private void removeRow(int rowIndex) throws Exception { 
		HtmlElement row = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_ProductExpenses2__expenses___" + rowIndex); 
		HtmlElement removeIcon = row.getElementsByTagName("a").get(0).getElementsByTagName("i").get(0); 
		removeIcon.click();		
	}

			
}
