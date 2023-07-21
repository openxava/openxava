package org.openxava.test.tests;

import org.htmlunit.*;
import org.htmlunit.html.*;
import org.openxava.tests.*;

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

	public void testAttributeOverridesOnEmbeddable_editorsMarkedAsErrorWithRepeatedEmbedded_attributeOverrideOnPrivateField_propertyNotBackedByField() throws Exception { // tmr		
		assertValueInList(0, "name", "MY OFFICE");
		assertValueInList(0, "address.street", "CUBA");
		assertValueInList(0, "address.zipCode", "49003");
		assertValueInList(0, "address.city", "VALENCIA");		
		assertNotExists("conditionValue.3"); // We cannot filter by zipCode because has a no a corresponding 'zipCode' field // tmr
		assertNoAction("List.orderBy", "property=address.zipCode");
		
		execute("List.viewDetail", "row=0");
		assertEditable("address.zipCode"); // tmr
		execute("Building.save");
		HtmlElement addressStreet = getHtmlPage().getHtmlElementById("ox_openxavatest_Building__editor_address___street");
		assertFalse(addressStreet.getAttribute("class").contains("ox-error-editor"));
		HtmlElement addressState = getHtmlPage().getHtmlElementById("ox_openxavatest_Building__reference_editor_address___state");
		assertFalse(addressState.getAttribute("class").contains("ox-error-editor"));
		
		HtmlElement mailingAddressStreet = getHtmlPage().getHtmlElementById("ox_openxavatest_Building__editor_mailingAddress___street");
		assertTrue(mailingAddressStreet.getAttribute("class").contains("ox-error-editor"));		
		HtmlElement mailingAddressState = getHtmlPage().getHtmlElementById("ox_openxavatest_Building__reference_editor_mailingAddress___state");
		assertTrue(mailingAddressState.getAttribute("class").contains("ox-error-editor"));
		
		assertErrorsCount(4);
		assertError("Value for Zip code in Mailing address is required"); // Mailing address (the member name) and not Address (the model name)
		assertError("Value for City in Mailing address is required");
		assertError("Value for Street in Mailing address is required");
		assertError("Value for State in Mailing address is required");
	}
	
	public void testConfirmDefaultAction() throws Exception { 
		MessageConfirmHandler confirmHandler = new MessageConfirmHandler();
		getWebClient().setConfirmHandler(confirmHandler);
		execute("CRUD.new");
		assertMessage("OnChangeVoidAction executed"); // To assure that there are a property with @OnChange, needed for tests of BuildingSimpleTest
		executeDefaultAction();
		assertEquals("Save the current entity: Are you sure?", confirmHandler.getMessage());
	}
	
}
