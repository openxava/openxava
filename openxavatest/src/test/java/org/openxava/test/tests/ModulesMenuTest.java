package org.openxava.test.tests;

import org.openqa.selenium.*;

/**
 * tmr
 * To test left modules list menu related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class ModulesMenuTest extends WebDriverTestBase {
	
	private WebDriver driver;

	public void setUp() throws Exception {
	    driver = createWebDriver();
	}
	
	public void tearDown() throws Exception {
		driver.quit();
	}
	
	public void testHideShowModulesMenu() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Applicant"); // Whatever
		wait(driver);
		
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
		Thread.sleep(500);
		
		assertFalse(modulesList.isDisplayed()); 
		assertTrue(menuButton.isDisplayed());
		assertTrue(extendedTitle.isDisplayed());
		assertFalse(hideButton.isDisplayed());
		
		showButton.click();
		Thread.sleep(500);		
		
		assertTrue(modulesList.isDisplayed());
		assertFalse(menuButton.isDisplayed());
		assertFalse(extendedTitle.isDisplayed());
		assertTrue(hideButton.isDisplayed());
		
		hideButton.click();
		Thread.sleep(500);
		
		assertFalse(modulesList.isDisplayed());
		assertTrue(menuButton.isDisplayed());
		assertTrue(extendedTitle.isDisplayed());
		assertFalse(hideButton.isDisplayed());
		
		menuButton.click();
		Thread.sleep(500);		
		
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
