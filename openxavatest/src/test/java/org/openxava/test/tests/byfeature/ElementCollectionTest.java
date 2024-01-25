package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;

/**
 * To test @ElementCollection related issues with Selenium.
 *
 * @author Javier Paniza
 */

public class ElementCollectionTest extends WebDriverTestBase {
		
	public void testFocus() throws Exception {
		goModule("Quote"); 
		execute("CRUD.new");
		setValueInCollection("details", 0, "product.number", "1\t");
		wait(getDriver());
		WebElement focusedElement = getDriver().switchTo().activeElement();
		assertEquals("ox_openxavatest_Quote__details___0___unitPrice", focusedElement.getAttribute("name"));
	}

	private void setValueInCollection(String collection, int row, String property, String value) throws Exception {
		setValue(collection + "." + row + "." + property, value);
	}

}
