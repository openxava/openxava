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
	
	
	public void testRowActionsGroupInPopUp() throws Exception {
		goModule("Carrier");
		List<WebElement> menuIcons = getDriver().findElements(By.id("xava_popup_menu_icon"));
		assertTrue(menuIcons.isEmpty());
		execute("List.viewDetail", "row=0");
		menuIcons = getDriver().findElements(By.id("xava_popup_menu_icon"));
		assertTrue(menuIcons.size() == 6); // normal coll and calculated coll
		List<WebElement> menu = getDriver().findElements(By.id("xava_popup_menu"));
		assertTrue(menu.get(0).getAttribute("class").contains("ox-display-none"));
		assertTrue(menu.get(0).findElements(By.tagName("li")).size() == 2);
		menuIcons.get(0).click();
		Thread.sleep(100);
		assertTrue(!menu.get(0).getAttribute("class").contains("ox-display-none"));
		assertTrue(menu.get(0).findElements(By.tagName("li")).size() == 1);
		menu.get(0).findElement(By.tagName("a")).click();
		wait(getDriver());
		assertTrue(getDriver().findElements(By.cssSelector(".ox_openxavatest_Carrier__tipable.ox_openxavatest_Carrier__fellowCarriers_col1")).get(0).getText().equals("TWO"));
		
		menuIcons = getDriver().findElements(By.id("xava_popup_menu_icon"));
		menuIcons.get(0).click();
		menu = getDriver().findElements(By.id("xava_popup_menu"));
		menu.get(0).findElement(By.tagName("a")).click();
	}

}
