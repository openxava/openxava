package org.openxava.test.tests.byfeature;

import java.time.*;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.support.ui.*;

import junit.framework.*;

/**
 * Base class to test using Selenium WebDriver
 * 
 * @author Javier Paniza
 */
abstract public class WebDriverTestBase extends TestCase {
	
	private boolean headless = false;
	protected String module;
	
	protected WebDriver createWebDriver() {
		ChromeOptions options = new ChromeOptions();
	    options.addArguments("--remote-allow-origins=*");
	    options.addArguments("--accept-lang=en");
	    options.addArguments("--lang=en"); 
	    
	    //Sometime needs set path and update manually chromedriver when chrome just been updated
	    //https://googlechromelabs.github.io/chrome-for-testing/
	    //System.setProperty("webdriver.chrome.driver", "C:/Program Files/Google/Chrome/Application/chromedriver.exe");
	    if (isHeadless()) {
		    options.addArguments("--headless"); 
		    options.addArguments("--disable-gpu"); 	    	
	    }
		return new ChromeDriver(options);
	}
	
	protected WebDriver createWebDriver(String lang) {
		ChromeOptions options = new ChromeOptions();
	    options.addArguments("--remote-allow-origins=*");
	    options.addArguments("--accept-lang=" + lang);

		return new ChromeDriver(options);
	}
		
	protected void wait(WebDriver driver) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(100));
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("xava_loading")));
		}
		catch (Exception ex) {
		}
		wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("xava_loading"))); 
	}
	
	protected void wait(WebDriver driver, By expectedElement) { 
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3)); 
        wait.until(ExpectedConditions.visibilityOfElementLocated(expectedElement));		
	}
	
    protected void wait(WebDriver driver, WebElement expectedElement, boolean visible) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
        if (visible) {
        	wait.until(ExpectedConditions.visibilityOf(expectedElement));
        } else {
        	wait.until(ExpectedConditions.invisibilityOf(expectedElement));
        }
    }

	protected void acceptInDialogJS(WebDriver driver) throws Exception {
		//use it after verifying that the test works well. 
		//it helps to avoid errors when starting the browser with the module.
		//can use too for click Accept case.
		try {
			Alert alert = driver.switchTo().alert();
			alert.accept();
			alert.dismiss();
			wait(driver);
		} catch(NoAlertPresentException e) {
		}
	}
	
	protected void execute(WebDriver driver, String action) throws Exception {
		executeWithoutArg(driver, this.module, action);
	}
    
	protected void execute(WebDriver driver, String action, String arguments) throws Exception {
		executeWithArg(driver, module, action, arguments);
	}
	
	protected void executeWithoutArg(WebDriver driver, String moduleName, String action) throws Exception {
		String[] actionS = action.split("\\.");
		WebElement button = driver.findElement
				(By.id("ox_openxavatest_" + moduleName + "__" + actionS[0] + "___" + actionS[1]));
		button.click();
		acceptInDialogJS(driver);
		wait(driver);
		//if back to CalendarView, need add another wait after this method
		//waitCalendarEvent(driver);
	}
	
	protected void executeWithArg(WebDriver driver, String moduleName, String action, String arguments) throws Exception { 
		try { 
			WebElement button = driver.findElement(By.cssSelector("a[data-action='" + action + "'][data-argv='" + arguments + "']"));
			button.click();
			acceptInDialogJS(driver);
			wait(driver);
		}
		catch (NoSuchElementException ex) {
			if (arguments.startsWith(",")) throw ex;
			executeWithArg(driver, moduleName, action, "," + arguments);
		}
	}
	
	protected void clickOnButtonWithId(WebDriver driver, String id) throws Exception {
		WebElement button = driver.findElement(By.id(id));
		button.click();
		wait(driver);
	}

	protected void clickOnSectionWithChildSpanId(WebDriver driver, String id) throws Exception {
		WebElement span = driver.findElement(By.id(id));
		WebElement a = span.findElement(By.xpath(".."));
		a.click();
		wait(driver);
	}
	
	protected void insertValueToInput(WebDriver driver, String id, String value, boolean delete) {
		WebElement inputElement = driver.findElement(By.id(id));
		if (delete == true) inputElement.clear();
        inputElement.sendKeys(value);
	}

	protected boolean isHeadless() {
		return headless;
	}

	protected void setHeadless(boolean headless) {
		this.headless = headless;
	}
	
	protected void blur(WebDriver driver, WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].blur();", element);
    }
	
	protected void goModule(WebDriver driver, String module) throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/" + module);
		wait(driver);
		acceptInDialogJS(driver);
		this.module = module;
	}
	
	protected void moveToListView(WebDriver driver) throws Exception {
		WebElement tabList = driver.findElement(By.cssSelector(".mdi.mdi-table-large"));
		WebElement tabListParent = tabList.findElement(By.xpath(".."));
		String title = tabListParent.getAttribute("class");
		if (!(title != null && title.equals("xava_action ox-selected-list-format"))) {
			tabList.click();
		}
		wait(driver);
	}
	
	protected void assertListColumnCount(WebDriver driver, int expectedColumnCount) {
		assertCollectionColumnCount(driver, "list", expectedColumnCount);
	}
	
	protected void assertCollectionColumnCount(WebDriver driver, String collection, int expectedColumnCount) {
		assertEquals(expectedColumnCount, getCollectionColumnCount(driver, collection));
	}
	
	protected int getCollectionColumnCount(WebDriver driver, String collection) {
		int columnCount = getTable(driver, collection).findElement(By.tagName("tr")).findElements(By.tagName("th")).size();
		return columnCount - 2;
	}

	protected void assertListRowCount(WebDriver driver, int expectedRowCount) {
		assertEquals(expectedRowCount, getListRowCount(driver));
	}
	
	protected int getListRowCount(WebDriver driver) { 
		return getCollectionRowCount(driver, "list") - 2;
	}
	
	protected void assertCollectionRowCount(WebDriver driver, String collection, int expectedRowCount) {
		assertEquals(expectedRowCount, getCollectionRowCount(driver, collection));
	}
	
	protected int getCollectionRowCount(WebDriver driver, String collection) { 
		int rowCount = getTable(driver, collection).findElements(By.tagName("tr")).size();
		return rowCount - 1;
	}
	
	private WebElement getTable(WebDriver driver, String collection) { 
		return driver.findElement(By.id("ox_openxavatest_" + module + "__" + collection));
	}
	/*
	protected String getValueInList(WebDriver driver, String module, String row, String column) {
		List<WebElement> elements = driver.findElements(By.cssSelector(".ox_openxavatest_" + module + "__tipable.ox_openxavatest_" + module + "__list_col" + column ));
		return elements.get(Integer.valueOf(row)).getText();
	}
	
	protected String getValueInCollection(WebDriver driver, String module, String collection, String row, String column) {
		List<WebElement> elements = driver.findElements(By.cssSelector(".ox_openxavatest_" + module + "__tipable.ox_openxavatest_" + module + "__" + collection + "_col" + column ));
		return elements.get(Integer.valueOf(row)).getText();
	}*/
	
	protected void assertValueInList(WebDriver driver, int row, int column, String expectedValue) { // Duplicated with DescriptionsListTest, refactoring pending 
		assertEquals(expectedValue, getValueInList(driver, row, column));				
	}
	
	protected void assertValueInCollection(WebDriver driver, String collection, int row, int column, String expectedValue) {
		assertEquals(expectedValue, getValueInCollection(driver, collection, row, column));				
	}
	
	protected String getValueInList(WebDriver driver, int row, int column) { // Duplicated with DescriptionsListTest, refactoring pending
		return getValueInCollection(driver, "list", row + 1, column);
	}
	
	protected String getValueInCollection(WebDriver driver, String collection, int row, int column) { // Duplicated with DescriptionsListTest, refactoring pending
		return getCell(driver, collection, row + 1, column).getText().trim();
	}
	
	private WebElement getCell(WebDriver driver, String collection, int row, int column) { // Duplicated with DescriptionsListTest, refactoring pending
		WebElement tableRow = getTable(driver, collection).findElements(By.tagName("tr")).get(row);
		String cellType = row == 0?"th":"td";
		List<WebElement> cells = tableRow.findElements(By.tagName(cellType));		
		return cells.get(column + 2);
	}
	
	protected void assertLabelInList(WebDriver driver, int column, String expectedLabel) { 
		assertLabelInCollection(driver,"list", column, expectedLabel);
	}
	
	protected void assertLabelInCollection(WebDriver driver, String collection, int column, String expectedLabel) {
		String label = getHeader(driver,collection, column).getText().trim();
		assertEquals(expectedLabel, label);
	}
	
	protected WebElement getHeader(WebDriver driver, String collection, int column) {
		return getCell(driver, collection, 0, column);
	}
	
	protected void moveColumn(WebDriver driver, int sourceColumn, int targetColumn) throws Exception {
		moveColumn(driver, "list", sourceColumn, targetColumn);
	}
	
	protected void moveColumn(WebDriver driver, String collection, int sourceColumn, int targetColumn) throws Exception {
		WebElement handle = getHeader(driver, collection, sourceColumn).findElement(By.className("xava_handle")); 
		String classTargetPoint = sourceColumn > targetColumn?"xava_handle":"mdi-close-circle"; 
		WebElement targetPoint = getHeader(driver, collection, targetColumn).findElement(By.className(classTargetPoint));
		Actions actions = new org.openqa.selenium.interactions.Actions(driver);
		actions.dragAndDrop(handle, targetPoint).build().perform();
		wait(driver);
	}
	
	protected void removeColumn(WebDriver driver, int columnIndex) throws Exception {
		removeColumn(driver, "list", columnIndex);
	}
	
	protected void removeColumn(WebDriver driver, String collection, int columnIndex) throws Exception {
		WebElement removeButton = getHeader(driver, collection, columnIndex).findElement(By.className("mdi-close-circle"));
		removeButton.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(1000));
		wait.until(ExpectedConditions.invisibilityOf(removeButton));
		wait(driver);
	}
	
}
