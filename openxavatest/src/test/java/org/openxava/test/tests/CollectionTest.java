package org.openxava.test.tests;

import org.openqa.selenium.*;

/**
 * tmr
 * To test collections related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class CollectionTest extends WebDriverTestBase {
	
	private WebDriver driver;

	public void setUp() throws Exception {
	    driver = createWebDriver();
	}
	
	public void tearDown() throws Exception {
		driver.quit();
	}
	
	public void testNoFilterInCollectionByDefault() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Author"); 
		wait(driver);

		execute(driver, "Author", "CRUD.new");		
		assertCollectionFilterNotDisplayed();
		// TMR ME QUEDÉ POR AQUÍ, HACIENDO EL TEST
		/* tmr
		getHtmlPage().getHtmlElementById("ox_openxavatest_Author__show_filter_humans").click();
		assertCollectionFilterDisplayed();
		
		execute("MyGoListMode.list");  
		execute("List.viewDetail", "row=1");
		assertCollectionFilterNotDisplayed();
		getHtmlPage().getHtmlElementById("ox_openxavatest_Author__show_filter_humans").click();
		assertCollectionFilterDisplayed();
		
		execute("CRUD.new");
		assertCollectionFilterNotDisplayed();
		getHtmlPage().getHtmlElementById("ox_openxavatest_Author__show_filter_humans").click();
		assertCollectionFilterDisplayed();
		getHtmlPage().getHtmlElementById("ox_openxavatest_Author__hide_filter_humans").click();
		Thread.sleep(1000);
		assertCollectionFilterNotDisplayed();
		*/
	}
	
	private void assertCollectionFilterDisplayed() { 
		assertTrue(driver.findElement(By.id("ox_openxavatest_Author__xava_collectionTab_humans_conditionValue___0")).isDisplayed());
	}
	
	private void assertCollectionFilterNotDisplayed() { 
		assertFalse(driver.findElement(By.id("ox_openxavatest_Author__xava_collectionTab_humans_conditionValue___0")).isDisplayed());
	}

		
}
