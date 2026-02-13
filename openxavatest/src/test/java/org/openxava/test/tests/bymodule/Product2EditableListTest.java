package org.openxava.test.tests.bymodule;

import org.htmlunit.html.*;

import org.openxava.test.model.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class Product2EditableListTest extends ModuleTestBase {
	
	public Product2EditableListTest(String testName) {
		super(testName, "Product2EditableList");		
	}
	
	public void testEditablePropertiesInList() throws Exception {
		// Simple property
		assertValueInList(0, "unitPrice", "11.00"); 
		assertValueInList(1, "unitPrice", "20.00");
		assertValueInList(2, "unitPrice",  "0.00");
		assertEditableInList(0, "unitPrice");
		assertEditableInList(1, "unitPrice");
		assertEditableInList(2, "unitPrice");
		
		// @DescriptionsList with single key
		assertValueInList(0, "family.description", "SOFTWARE"); 
		assertValueInList(1, "family.description", "HARDWARE");
		assertValueInList(2, "family.description", "SOFTWARE");
		assertEditableInList(0, "family.description");
		assertEditableInList(1, "family.description");
		assertEditableInList(2, "family.description");

		// @DescriptionsList with multiple keys
		assertValueInList(0, "warehouse.name", "ALMA 11");
		assertValueInList(1, "warehouse.name", "CENTRAL VALENCIA");
		assertValueInList(2, "warehouse.name", "VALENCIA NORTE");
		assertEditableInList(0, "warehouse.name");
		assertEditableInList(1, "warehouse.name");
		assertEditableInList(2, "warehouse.name");

		// Included in editableProperties but it's @Formula so not should be editable
		assertValueInList(0, "extendedDescription", "1 - MULTAS DE TRAFICO");
		assertValueInList(1, "extendedDescription", "2 - IBM ESERVER ISERIES 270");
		assertValueInList(2, "extendedDescription", "3 - XAVA");		
		assertNoEditableInList(0, "extendedDescription"); 
		assertNoEditableInList(1, "extendedDescription");
		assertNoEditableInList(2, "extendedDescription");

		// No refence actions
		assertNoAction("Reference.createNew");
		
		// Modifying simple property
		setValueInList(0, "unitPrice", "17");
		assertNoErrors();
		assertMessage("Saved new value for unit price in row 1. Undo");
		
		setValueInList(1, "unitPrice", "31");
		assertNoErrors();
		assertMessage("Saved new value for unit price in row 2. Undo");
		
		// Undo changes for simple property
		clickUndoInMessage();
		assertNoErrors();
		assertMessage("Restored previous value for unit price in row 2");
		assertValueInList(1, "unitPrice", "20.00");
		
		setValueInList(1, "unitPrice", "31");
		assertNoErrors();
		
		setValueInList(1, "unitPrice", "1500");
		assertError("{0} in {1} can not be greater than 1000"); // {0} and {1} should be replaced, but it fails too in detail mode, so it's another different bug
		
		// Modifying references as @DescriptionsList 
		setValueInList(1, "family.number", "3"); 
		assertMessage("Saved new value for family in row 2. Undo");
		
		// Undo changes for @DescriptionsList with single key
		clickUndoInMessage();
		assertErrorsNotDisplayed(); // assertNoErrors() fails because undo() hides errors via JS but doesn't remove them from HTML
		assertMessage("Restored previous value for family in row 2");
		assertValueInList(1, "family.description", "HARDWARE");
		
		setValueInList(1, "family.number", "3");
		assertErrorsNotDisplayed(); // assertNoErrors() fails because undo() hides errors via JS but doesn't remove them from HTML
		
		Warehouse warehouse = new Warehouse();
		warehouse.setZoneNumber(4);
		warehouse.setNumber(13);		 
		String warehouseKey = toKeyString(warehouse);
		setValueInList(1, "warehouse.KEY", warehouseKey); 
		assertMessage("Saved new value for warehouse in row 2. Undo");
		
		// Undo changes for @DescriptionsList with composite key
		clickUndoInMessage();
		assertErrorsNotDisplayed(); // assertNoErrors() fails because undo() hides errors via JS but doesn't remove them from HTML
		assertMessage("Restored previous value for warehouse in row 2");
		assertValueInList(1, "warehouse.name", "CENTRAL VALENCIA");
		
		setValueInList(1, "warehouse.KEY", warehouseKey);
		assertErrorsNotDisplayed(); // assertNoErrors() fails because undo() hides errors via JS but doesn't remove them from HTML
		
		// Verifying in detail
		execute("List.viewDetail", "row=1");
		assertValue("unitPrice", "31.00");
		assertValue("family.number", "3");
		assertValue("warehouse.KEY", warehouseKey);
		
		// New values shown in list 
		execute("Mode.list");
		
		assertValueInList(0, "unitPrice", "17.00");
		assertValueInList(1, "unitPrice", "31.00");
		assertValueInList(2, "unitPrice",  "0.00");
		assertValueInList(0, "family.description", "SOFTWARE");
		assertValueInList(1, "family.description", "SERVICIOS");
		assertValueInList(2, "family.description", "SOFTWARE");
		assertValueInList(0, "warehouse.name", "ALMA 11");
		assertValueInList(1, "warehouse.name", "ALMA 13");
		assertValueInList(2, "warehouse.name", "VALENCIA NORTE");		
		
		// Restoring
		setValueInList(0, "unitPrice", "11.00");
		setValueInList(1, "unitPrice", "20.00");
		setValueInList(1, "family.number", "2");
		warehouse.setZoneNumber(1);
		warehouse.setNumber(1);
		setValueInList(1, "warehouse.KEY", toKeyString(warehouse));		

		// For a bug returing from Charts 
		execute("ListFormat.select", "editor=Charts");
		execute("ListFormat.select", "editor=List");
		assertNoErrors();
		assertValueInList(0, "unitPrice", "11.00");  // So the list is displayed
	}
	
	private void assertErrorsNotDisplayed() throws Exception {
		HtmlElement errorsDiv = getHtmlPage().getHtmlElementById(decorateId("errors"));
		String style = errorsDiv.getAttribute("style");
		assertFalse("There are errors and should not", !style.contains("display: none") && errorsDiv.asNormalizedText().trim().length() > 0);
	}
	
	private void clickUndoInMessage() throws Exception {
		HtmlTable table = (HtmlTable) getHtmlPage().getHtmlElementById(decorateId("messages_table"));
		HtmlTableCell cell = table.getCellAt(0, 0);
		HtmlAnchor undoLink = cell.getFirstByXPath(".//a[@class='ox-undo-link']");
		assertNotNull("Undo link not found in message", undoLink);
		undoLink.click();
		waitAJAX();
	}
	
}
