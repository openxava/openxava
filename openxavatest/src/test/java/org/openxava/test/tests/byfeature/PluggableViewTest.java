package org.openxava.test.tests.byfeature;

import java.util.*;

import org.openqa.selenium.*;

public class PluggableViewTest extends WebDriverTestBase {

	public void testPluggableViewTest() throws Exception {
		//has-type tested with CalendarTest
		goModule("City");
		assertTrue(hasPluggableIcon());
		goModule("Subfamily");
		assertTrue(hasPluggableIcon());
	}
	
	private boolean hasPluggableIcon() {
		List<WebElement> iconElements = getDriver().findElements(By.cssSelector("i.mdi.mdi-puzzle"));
		return !iconElements.isEmpty();
	}
	
}
