package org.openxava.test.tests.byfeature;

/**
 * To test any URL issues with Selenium.
 * 
 * @author Chungyen Tsai
 */
public class UrlTest extends WebDriverTestBase {
	
	public void testAccessingRecordByURL() throws Exception {
		goModule("CustomerLessThanFour");
		assertListRowCount(3);
		getDriver().get("http://localhost:8080/openxavatest/m/CustomerLessThanFour?detail=4");
		wait(getDriver());
		System.out.println("number");
		System.out.println(getValue("number"));
		assertFalse(getValue("number").equals("4"));
		
	}

		
}
