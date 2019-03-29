package org.openxava.test.tests;

import org.openxava.tests.ModuleTestBase;
import com.gargoylesoftware.htmlunit.*;

/**
 * 
 * @author Javier Paniza
 */
public class BuildingTest extends ModuleTestBase {
	
	private class MessageConfirmHandler implements ConfirmHandler { 

		private String message;
		
		public boolean handleConfirm(Page page, String message) {
			this.message = message; 
			return true;
		}
		
		public String getMessage() {
			return message;
		}

	}
	
	public BuildingTest(String testName) {
		super(testName, "Building");		
	}

	public void testAttributeOverridesOnEmbeddable() throws Exception {		
		assertValueInList(0, "name", "MY OFFICE");
		assertValueInList(0, "address.street", "CUBA");
		assertValueInList(0, "address.zipCode", "49003");
		assertValueInList(0, "address.city", "VALENCIA");		
	}
	
	public void testConfirmDefaultAction() throws Exception { 
		MessageConfirmHandler confirmHandler = new MessageConfirmHandler();
		getWebClient().setConfirmHandler(confirmHandler);
		execute("CRUD.new");
		executeDefaultAction();
		assertEquals("Save the current entity: Are you sure?", confirmHandler.getMessage());
	}
	
}
