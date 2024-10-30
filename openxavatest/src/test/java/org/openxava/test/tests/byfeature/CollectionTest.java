package org.openxava.test.tests.byfeature;

import java.util.*;

import org.openqa.selenium.*;

/**
 * To test collections related issues with Selenium. <p>
 * 
 * List features present in @OneToMany collections, like sorting,
 * customizing, filtering, paging, etc. are tested in ListTest.   
 * 
 * @author Chungyen Tsai
 */
public class CollectionTest extends WebDriverTestBase {
	
	public void testChangeSectionNotLoadCollection() throws Exception {
		goModule("Invoice");
		execute("CRUD.new");
		setValue("year", "2002");
		setValue("number", "1");
		execute("Sections.change", "activeSection=1");
		assertCollectionRowCount("details", (0+2));
		execute("CRUD.refresh");
		assertCollectionRowCount("details", (2+2)); 
		
		goModule("CustomerWithSection");
		execute("CRUD.new");
		setValue("number", "43");
		execute("Sections.change", "activeSection=1");
		assertCollectionRowCount("states", 0);
		execute("CRUD.refresh"); 
		assertCollectionRowCount("states", 1);
		
		goModule("Office"); 
		execute("CRUD.new");
		setValue("defaultCarrier.number","1");
		WebElement label = getDriver().findElement(By.id("ox_openxavatest_Office__label_defaultCarrier___number"));
		label.click();
		wait(getDriver());
		assertCollectionRowCount("defaultCarrier___fellowCarriers", (3+2));
		assertCollectionRowCount("defaultCarrier___fellowCarriersCalculated", (3+1));
	}
	
	
	public void testRowActionsGroupInPopUp_openRecordInNewWindow() throws Exception {
		goModule("Carrier");
		List<WebElement> menuIcons;
		List<WebElement> menu;
		execute("List.viewDetail", "row=0");
		menuIcons = getDriver().findElements(By.className("xava_popup_menu_icon"));
		assertTrue(menuIcons.size() == 6);
		menu = getDriver().findElements(By.className("ox-popup-menu"));
		assertTrue(menu.get(3).getAttribute("class").contains("ox-display-none"));
		menuIcons.get(3).click();
		Thread.sleep(100);
		assertFalse(menu.get(3).getAttribute("class").contains("ox-display-none"));
		menuIcons.get(3).click();
		Thread.sleep(100);
		assertTrue(menu.get(3).getAttribute("class").contains("ox-display-none"));
		menuIcons.get(3).click();
		Thread.sleep(100);
		assertTrue(menu.get(3).findElements(By.tagName("li")).size() == 5);
		assertTrue(!menu.get(3).getAttribute("class").contains("ox-display-none"));
		menu.get(3).findElements(By.tagName("a")).get(3).click();
		wait(getDriver());
		assertEquals("TWO ", getDriver().findElements(By.cssSelector(".ox_openxavatest_Carrier__tipable.ox_openxavatest_Carrier__fellowCarriersCalculated_col1")).get(0).getText());
		menuIcons = getDriver().findElements(By.className("xava_popup_menu_icon"));
		menuIcons.get(3).click();
		menu = getDriver().findElements(By.className("ox-popup-menu"));
		menu.get(3).findElements(By.tagName("a")).get(3).click();
		
		goModule("Invoice");
		menuIcons = getDriver().findElements(By.className("xava_popup_menu_icon"));
		assertTrue(menuIcons.isEmpty());
		execute("List.viewDetail", "row=0");
		execute("Sections.change", "activeSection=3");
		menuIcons = getDriver().findElements(By.className("xava_popup_menu_icon"));
		assertTrue(menuIcons.isEmpty());
		execute("Sections.change", "activeSection=1");
		menuIcons = getDriver().findElements(By.className("xava_popup_menu_icon"));
		menuIcons.get(0).click();
		menu = getDriver().findElements(By.className("ox-popup-menu"));
		assertTrue(menu.get(0).findElements(By.tagName("li")).size() == 3);
		
		String mainWindow = getDriver().getWindowHandle();
		menu.get(0).findElements(By.tagName("a")).get(1).click();
		Set<String> allWindows = getDriver().getWindowHandles();
		String newWindow = null;
		for (String handle : allWindows) {
		    if (!handle.equals(mainWindow)) {
		        newWindow = handle;
		        break;
		    }
		}
		
		getDriver().switchTo().window(newWindow);
		assertEquals("http://localhost:8080/openxavatest/m/InvoiceDetail?detail=2002:1:0", getDriver().getCurrentUrl());
		wait(getDriver());
		WebElement number = getDriver().findElement(By.id("ox_openxavatest_InvoiceDetail__product___number"));
		assertEquals("2", number.getAttribute("value"));
		
		getDriver().close();
		getDriver().switchTo().window(mainWindow);
		
		goModule("Project");
		execute("List.viewDetail", "row=0");
		mainWindow = getDriver().getWindowHandle();
		
		execute("CollectionOpenInNewTab.openInNewTab", "row=0,viewObject=xava_view_members");
		
		allWindows = getDriver().getWindowHandles();
		newWindow = null;
		for (String handle : allWindows) {
		    if (!handle.equals(mainWindow)) {
		        newWindow = handle;
		        break;
		    }
		}
		
		getDriver().switchTo().window(newWindow);
		assertEquals("http://localhost:8080/openxavatest/m/ProjectMember?detail=ff8080824d095a71014d0967110a0005", getDriver().getCurrentUrl());
		wait(getDriver());
		
		getDriver().close();
		getDriver().switchTo().window(mainWindow);
		
		
	}

}
