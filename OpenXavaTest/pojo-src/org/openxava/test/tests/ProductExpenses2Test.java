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
		
	public void testDescriptionsListWithDefaultValueInElementCollection() throws Exception {  // tmp Modificar nombre
		getWebClient().getOptions().setCssEnabled(true); // tmp
		execute("CRUD.new");
		setValue("description", "JUNIT EXPENSES");
		
		assertValueInCollection("expenses", 0, "invoice.KEY", "");
		assertValueInCollection("expenses", 0, "product.number", ""); 				
		setValueInCollection("expenses", 0, "carrier.number", "3");  
		assertValueInCollection("expenses", 0, "invoice.KEY", "[.1.2002.]"); 
		assertValueInCollection("expenses", 0, "product.number", "2");	
		
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("description", "JUNIT EXPENSES");
		assertCollectionRowCount("expenses", 1);
		assertValueInCollection("expenses", 0, "carrier.number", "3");  
		assertValueInCollection("expenses", 0, "invoice.KEY", "[.1.2002.]"); 
		assertValueInCollection("expenses", 0, "product.number", "2");	
		
		// tmp ini
		setValueInCollection("expenses", 1, "carrier.number", "2");
		setValueInCollection("expenses", 2, "carrier.number", "1");
		// TMP ME QUEDÉ POR AQUÍ, LO DE VERIFICAR EL COMBO ABIERTO NO ME FUNCIONA, PERO PUEDO COMPROBAR
		// TMP   QUE ICONO ESTÁ VISIBLE, EL DE ABRIR O EL DE CERRAR, LO QUE SERÍA MÁS FÁCIL Y SUFICIENTE
		// TMP ADEMÁS: LO QUE SE TESTEA ARRIBA FALLA EN REAL (AUNQUE EL TEST FUNCIONA) QUIERO PROBAR CON 6.2
		assertComboOpens(0, 18);
		assertComboOpens(0, 21);
		assertComboOpens(0, 24);
		printHtml(); // tmp
		// tmp fin
		
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	private void assertComboOpens(int row, int uiId) throws Exception { // tmp Refactor
		HtmlElement editor = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_ProductExpenses2__reference_editor_expenses___" + row + "___product");
		HtmlElement handler = editor.getElementsByTagName("i").get(0);
		assertTrue(!getHtmlPage().getHtmlElementById("ui-id-" + uiId).isDisplayed());
		handler.click();
		assertTrue(getHtmlPage().getHtmlElementById("ui-id-" + uiId).isDisplayed());		
	}
	
	private void removeRow(int rowIndex) throws Exception { // tmp Refactor
		HtmlElement row = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_ProductExpenses2__expenses___" + rowIndex); 
		HtmlElement removeIcon = row.getElementsByTagName("a").get(0).getElementsByTagName("i").get(0); 
		removeIcon.click();		
	}

			
}
