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
		//select second as current
		boolean hasSelectedChild = !modulesList.get(1).findElements(By.className("selected")).isEmpty();
		if (!hasSelectedChild) {
			modulesList.get(1).findElement(By.tagName("a")).click();
			wait(getDriver());
		}
		modulesList = getDriver().findElements(By.className("module-header-tab"));
		String fourth = modulesList.get(3).findElement(By.tagName("a")).getText();
		
		//close third
		WebElement icon = modulesList.get(2).findElement(By.className("close-icon"));
		icon.click();
		getDriver().navigate().refresh();
		wait(getDriver());
		
		//close fourth
		modulesList = getDriver().findElements(By.className("module-header-tab"));
		assertTrue(modulesList.get(2).findElement(By.tagName("a")).getText().equals(fourth));
		modulesList.get(2).findElement(By.className("close-icon")).click();
		getDriver().navigate().refresh();
		wait(getDriver());
		
		//assert new third(five). close second, new second is five
		modulesList = getDriver().findElements(By.className("module-header-tab"));
		assertFalse(modulesList.get(2).findElement(By.tagName("a")).getText().equals(fourth));
		String newThird = modulesList.get(2).findElement(By.tagName("a")).getText();
		modulesList.get(1).findElement(By.className("close-icon")).click();
		waitAndRefresh();
		
		modulesList = getDriver().findElements(By.className("module-header-tab"));
		hasSelectedChild = !modulesList.get(1).findElements(By.className("selected")).isEmpty();
	    assertTrue(hasSelectedChild);
	    modulesList = getDriver().findElements(By.className("module-header-tab"));
	    assertEquals(newThird , modulesList.get(1).findElement(By.tagName("span")).getText());
	    
	    //close all except 1,2
	    modulesList = getDriver().findElements(By.className("module-header-tab"));
	    System.out.println("antes");
		if (modulesList.size() != 1) {
		    for (WebElement module : modulesList) {
		        System.out.println(module.getAttribute("outerHTML"));
		    }
		}
	    for (int i =  modulesList.size(); i > 2; i--) {
	    	System.out.println("se trat de eliminar");
	    	System.out.println(modulesList.get(i-1).getAttribute("outerHTML"));
	    	modulesList.get(i-1).findElement(By.className("close-icon")).click();
	    }
	    
	    
	  //force close to keep with two modules, close 2
	    modulesList = getDriver().findElements(By.className("module-header-tab"));
		if (modulesList.size() != 1) {
			waitAndRefresh();
			int i = getDriver().findElements(By.className("module-header-tab")).size();
		    while (i > 2) {
		    	modulesList.get(i-1).findElement(By.className("close-icon")).click();
		    	i = getDriver().findElements(By.className("module-header-tab")).size();
		    }
		}
	    
		modulesList = getDriver().findElements(By.className("module-header-tab"));
	    modulesList.get(1).findElement(By.className("close-icon")).click();
	    waitAndRefresh();
		
		//close last one
		modulesList = getDriver().findElements(By.className("module-header-tab"));
		if (modulesList.size() != 1) {
		    for (WebElement module : modulesList) {
		        System.out.println(module.getAttribute("outerHTML"));
		    }
		}
		assertTrue(modulesList.size() == 1);
		modulesList.get(0).findElement(By.className("close-icon")).click();
		waitAndRefresh();
		modulesList = getDriver().findElements(By.className("module-header-tab"));
		assertTrue(modulesList.size() == 1);
		
		//open new module
		WebElement animal = getDriver().findElement(By.id("Animal_module"));
		JavascriptExecutor executor = (JavascriptExecutor) getDriver();
        executor.executeScript("arguments[0].click();", animal);
		waitAndRefresh();
		modulesList = getDriver().findElements(By.className("module-header-tab"));
		assertTrue(modulesList.size() == 2);
	}
	
	private void waitAndRefresh() throws Exception {
		wait(getDriver()); // if success or necessary
		getDriver().navigate().refresh();
		wait(getDriver());
	}
	
	
}
