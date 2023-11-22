package org.openxava.test.tests.byfeature;

import org.openqa.selenium.*;

/**
 * To test left modules list menu related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class ModulesMenuTest extends WebDriverTestBase {
	
	private WebDriver driver;

	public void setUp() throws Exception {
		setHeadless(true); 
	    driver = createWebDriver();
	}
	
	public void tearDown() throws Exception {
		driver.quit();
	}
	
	public void testHideShowModulesMenu() throws Exception {
		goTo(driver, "http://localhost:8080/openxavatest/m/Applicant"); // Whatever
		showModulesList(driver);
		
		WebElement modulesList = driver.findElement(By.id("modules_list"));
		WebElement menuButton = driver.findElement(By.id("module_header_menu_button"));
		WebElement extendedTitle = driver.findElement(By.id("module_extended_title"));
		WebElement hideButton = driver.findElement(By.cssSelector("#modules_list_hide i"));
		WebElement showButton = driver.findElement(By.id("modules_list_show"));

		assertTrue(modulesList.isDisplayed());
		assertFalse(menuButton.isDisplayed());
		assertFalse(extendedTitle.isDisplayed());
		assertTrue(hideButton.isDisplayed());
		
		hideButton.click();
		wait(driver, modulesList, false);
		assertFalse(modulesList.isDisplayed()); 
		assertTrue(menuButton.isDisplayed());
		assertTrue(extendedTitle.isDisplayed());
		assertFalse(hideButton.isDisplayed());
		
		showButton.click();
		wait(driver, modulesList, true);
		assertTrue(modulesList.isDisplayed());
		assertFalse(menuButton.isDisplayed());
		assertFalse(extendedTitle.isDisplayed());
		assertTrue(hideButton.isDisplayed());
		
		hideButton.click();
		wait(driver, modulesList, false);
		assertFalse(modulesList.isDisplayed());
		assertTrue(menuButton.isDisplayed());
		assertTrue(extendedTitle.isDisplayed());
		assertFalse(hideButton.isDisplayed());
		
		menuButton.click();
		wait(driver, modulesList, true);
		assertTrue(modulesList.isDisplayed());
		assertFalse(menuButton.isDisplayed());
		assertFalse(extendedTitle.isDisplayed());
		assertTrue(hideButton.isDisplayed());					
	}
	
	protected void showModulesList(WebDriver driver) throws Exception {
		WebElement showListButton = driver.findElement(By.id("modules_list_show"));
		if (showListButton.isDisplayed()) {
			showListButton.click();
			wait(driver);
		}
	}
	
}
