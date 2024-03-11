package org.openxava.test.tests.byfeature;

import java.util.*;

import org.openqa.selenium.*;

/**
 * To test left modules list menu related issues with Selenium.
 * 
 * @author Javier Paniza
 */
public class ModulesMenuTest extends WebDriverTestBase {
	
	public void testHideShowModulesMenu() throws Exception {
		goModule("Applicant"); // Whatever
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
	
	public void testCloseModule() throws Exception {
		goModule("Invoice");
		List<WebElement> modulesList = getDriver().findElements(By.className("module-header-tab"));
		//5 modules at least
		if (modulesList.size() < 5) {
			goModule("Order");
			goModule("Appointment");
			goModule("Artist");
			goModule("Article");
			modulesList = getDriver().findElements(By.className("module-header-tab"));
		}
		
		//select fourth as current
		String fourth = modulesList.get(3).findElement(By.tagName("a")).getText();
		String fifth = modulesList.get(4).findElement(By.tagName("a")).getText();
		boolean hasSelectedChild = !modulesList.get(3).findElements(By.className("selected")).isEmpty();
		if (!hasSelectedChild) {
			modulesList.get(3).findElement(By.tagName("a")).click();
			wait(getDriver());
		}
		modulesList = getDriver().findElements(By.className("module-header-tab"));

		//close third
		WebElement icon = modulesList.get(2).findElement(By.className("close-icon"));
		icon.click();
		getDriver().navigate().refresh();
		wait(getDriver());
		
		//new third is fourth, close fourth
		modulesList = getDriver().findElements(By.className("module-header-tab"));
		assertTrue(modulesList.get(2).findElement(By.tagName("span")).getText().equals(fourth));
		modulesList.get(2).findElement(By.className("close-icon")).click();
		getDriver().navigate().refresh();
		wait(getDriver());
		
		//assert new fourth(five), close it
		modulesList = getDriver().findElements(By.className("module-header-tab"));
		assertTrue(modulesList.get(2).findElement(By.tagName("span")).getText().equals(fifth));
		modulesList.get(2).findElement(By.className("close-icon")).click();
		waitAndRefresh();
		
		//assert fixModules can't close
		modulesList = getDriver().findElements(By.className("module-header-tab"));
		hasSelectedChild = !modulesList.get(1).findElements(By.className("selected")).isEmpty();
	    assertTrue(hasSelectedChild);
	    assertTrue(modulesList.get(1).findElements(By.className("close-icon")).isEmpty());
	}
	
	private void waitAndRefresh() throws Exception {
		wait(getDriver()); // if success or necessary
		getDriver().navigate().refresh();
		wait(getDriver());
	}
	
	
}
