package org.openxava.test.byfeature;

import org.openqa.selenium.*;

/**
 * To test left modules list menu related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class ModulesMenuTest extends WebDriverTestBase {
	
	public void testHideShowModulesMenu() throws Exception {
		goModule(getDriver(), "Applicant"); // Whatever
		showModulesList();
		
		WebElement modulesList = getDriver().findElement(By.id("modules_list"));
		WebElement menuButton = getDriver().findElement(By.id("module_header_menu_button"));
		WebElement extendedTitle = getDriver().findElement(By.id("module_extended_title"));
		WebElement hideButton = getDriver().findElement(By.cssSelector("#modules_list_hide i"));
		WebElement showButton = getDriver().findElement(By.id("modules_list_show"));

		assertTrue(modulesList.isDisplayed());
		assertFalse(menuButton.isDisplayed());
		assertFalse(extendedTitle.isDisplayed());
		assertTrue(hideButton.isDisplayed());
		
		hideButton.click();
		wait(getDriver(), modulesList, false);
		assertFalse(modulesList.isDisplayed()); 
		assertTrue(menuButton.isDisplayed());
		assertTrue(extendedTitle.isDisplayed());
		assertFalse(hideButton.isDisplayed());
		
		showButton.click();
		wait(getDriver(), modulesList, true);
		assertTrue(modulesList.isDisplayed());
		assertFalse(menuButton.isDisplayed());
		assertFalse(extendedTitle.isDisplayed());
		assertTrue(hideButton.isDisplayed());
		
		hideButton.click();
		wait(getDriver(), modulesList, false);
		assertFalse(modulesList.isDisplayed());
		assertTrue(menuButton.isDisplayed());
		assertTrue(extendedTitle.isDisplayed());
		assertFalse(hideButton.isDisplayed());
		
		menuButton.click();
		wait(getDriver(), modulesList, true);
		assertTrue(modulesList.isDisplayed());
		assertFalse(menuButton.isDisplayed());
		assertFalse(extendedTitle.isDisplayed());
		assertTrue(hideButton.isDisplayed());					
	}
	
	protected void showModulesList() throws Exception {
		WebElement showListButton = getDriver().findElement(By.id("modules_list_show"));
		if (showListButton.isDisplayed()) {
			showListButton.click();
			wait(getDriver());
		}
	}
	
}
