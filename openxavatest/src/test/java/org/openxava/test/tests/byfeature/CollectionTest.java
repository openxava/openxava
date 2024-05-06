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
		
		goModule("Order"); 
		execute("CRUD.new");
		WebElement customerNumber = getDriver().findElement(By.id("ox_openxavatest_Order__customer___number"));
		customerNumber.sendKeys("1");
		customerNumber.sendKeys(Keys.TAB);
		getDriver().findElement(By.id("ox_openxavatest_Order__label_customer___number")).click();
		execute("Collection.new", "viewObject=xava_view_details");
		wait(getDriver());
		WebElement productNumber = getDriver().findElement(By.id("ox_openxavatest_Order__product___number"));
		productNumber.sendKeys("1");
		productNumber.sendKeys(Keys.TAB);
		WebElement quantity = getDriver().findElement(By.id("ox_openxavatest_Order__quantity"));
		quantity.sendKeys("1");
		execute("Collection.save");
		execute("Collection.new", "viewObject=xava_view_details");
		Thread.sleep(1000);
		List<WebElement> messagesElements = getDriver().findElements(By.className("ox-messages"));
		assertTrue(messagesElements.get(0).getAttribute("style").contains("display: none;"));
		execute("Collection.hideDetail");
		execute("CRUD.delete");
	}

}
