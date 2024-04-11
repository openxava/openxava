package org.openxava.test.tests.byfeature;

import java.time.*;
import java.util.*;

import org.apache.commons.logging.*;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.support.ui.*;
import org.openxava.util.*;
import org.openxava.web.*;

import junit.framework.*;

/**
 * Base class to test using Selenium WebDriver
 * 
 * @author Javier Paniza
 */
abstract public class WebDriverTestBase extends TestCase {
	
	private boolean headless = true;
	private String module;
	private WebDriver driver;
	
	protected void setUp() throws Exception {
		driver = createWebDriver("en");
	}

	protected void tearDown() throws Exception {
		driver.quit();
	}
	
	protected WebDriver createWebDriver(String lang) {
		ChromeOptions options = new ChromeOptions();
	    options.addArguments("--remote-allow-origins=*");
	    options.addArguments("--accept-lang=" + lang);
	    options.addArguments("--lang=" + lang); 
	    if (isHeadless()) {
		    options.addArguments("--headless"); 
		    options.addArguments("--disable-gpu"); 	    	
	    }
		return new ChromeDriver(options);
	}
	
	protected void changeLanguage(String lang) throws Exception {
		tearDown();
		driver = createWebDriver(lang);
	}
	
	protected String getModule() {
		return this.module;
	}
	
	protected WebDriver getDriver() {
		return this.driver;
	}
		
	protected void setDriver(WebDriver driver) {
		this.driver = driver;
	}
	
	protected void wait(WebDriver driver) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(300)); // 100 is too short, at least 300
		try {
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("xava_loading")));
		}
		catch (Exception ex) {
		}
		wait = new WebDriverWait(driver, Duration.ofSeconds(10)); 
		wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("xava_loading"))); //sometimes this code cause error in calendar, reset db and run again test
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
	
	protected void execute(String action) throws Exception {
		executeWithoutArg(this.module, action);
	}
    
	protected void execute(String action, String arguments) throws Exception {
		executeWithArg(module, action, arguments);
	}
	
	protected void executeWithoutArg(String moduleName, String action) throws Exception {
		String[] actionS = action.split("\\.");
		WebElement button = driver.findElement
				(By.id("ox_openxavatest_" + moduleName + "__" + actionS[0] + "___" + actionS[1]));
		button.click();
		acceptInDialogJS(driver);
		wait(driver);
		//if back to CalendarView, need add another wait after this method
		//waitCalendarEvent(driver);
	}
	
	protected void executeWithArg(String moduleName, String action, String arguments) throws Exception { 
		try { 
			WebElement button = driver.findElement(By.cssSelector("a[data-action='" + action + "'][data-argv='" + arguments + "']"));
			button.click();
			acceptInDialogJS(driver);
			wait(driver);
		}
		catch (NoSuchElementException ex) {
			if (arguments.startsWith(",")) throw ex;
			executeWithArg(moduleName, action, "," + arguments);
		}
	}
	
	protected void clickOnButtonWithId(String id) throws Exception {
		WebElement button = driver.findElement(By.id(id));
		button.click();
		wait(driver);
	}

	protected void clickOnSectionWithChildSpanId(String id) throws Exception {
		WebElement span = driver.findElement(By.id(id));
		WebElement a = span.findElement(By.xpath(".."));
		a.click();
		wait(driver);
	}

	protected boolean isHeadless() {
		return headless;
	}

	protected void setHeadless(boolean headless) {
		this.headless = headless;
	}
	
	protected void blur(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].blur();", element);
    }
	
	protected void goModule(String module) throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/" + module);
		acceptInDialogJS(driver);
		wait(driver);
		this.module = module;
		//this.driver = driver;
	}
	
	protected void moveToListView() throws Exception {
		WebElement tabList = driver.findElement(By.cssSelector(".mdi.mdi-table-large"));
		WebElement tabListParent = tabList.findElement(By.xpath(".."));
		String title = tabListParent.getAttribute("class");
		if (!(title != null && title.equals("xava_action ox-selected-list-format"))) {
			tabList.click();
		}
		wait(driver);
	}
	
	protected void assertListColumnCount(int expectedColumnCount) {
		assertCollectionColumnCount("list", expectedColumnCount);
	}
	
	protected void assertCollectionColumnCount(String collection, int expectedColumnCount) {
		assertEquals(expectedColumnCount, getCollectionColumnCount(collection));
	}
	
	protected int getCollectionColumnCount(String collection) {
		int columnCount = getTable(collection).findElement(By.tagName("tr")).findElements(By.tagName("th")).size();
		return columnCount - 2;
	}

	protected void assertListRowCount(int expectedRowCount) {
		assertEquals(expectedRowCount, getListRowCount(driver));
	}
	
	protected int getListRowCount(WebDriver driver) { 
		return getCollectionRowCount("list") - 2;
	}
	
	protected void assertCollectionRowCount(String collection, int expectedRowCount) {
		assertEquals(expectedRowCount, getCollectionRowCount(collection));
	}
	
	protected int getCollectionRowCount(String collection) { 
		int rowCount = getTable(collection).findElements(By.tagName("tr")).size();
		return rowCount - 1;
	}
	
	private WebElement getTable(String collection) { 
		return driver.findElement(By.id("ox_openxavatest_" + module + "__" + collection));
	}
	
	protected void assertValueInList(int row, int column, String expectedValue) { // Duplicated with DescriptionsListTest, refactoring pending 
		assertEquals(expectedValue, getValueInList(row, column));				
	}
	
	protected void assertValueInCollection(String collection, int row, int column, String expectedValue) {
		assertEquals(expectedValue, getValueInCollection(collection, row, column));				
	}
	
	protected String getValueInList(int row, int column) { // Duplicated with DescriptionsListTest, refactoring pending
		return getValueInCollection("list", row + 1, column);
	}
	
	protected String getValueInCollection(String collection, int row, int column) { // Duplicated with DescriptionsListTest, refactoring pending
		return getCell(collection, row + 1, column).getText().trim();
	}
	
	private WebElement getCell(String collection, int row, int column) { // Duplicated with DescriptionsListTest, refactoring pending
		WebElement tableRow = getTable(collection).findElements(By.tagName("tr")).get(row);
		String cellType = row == 0?"th":"td";
		List<WebElement> cells = tableRow.findElements(By.tagName(cellType));		
		return cells.get(column + 2);
	}
	
	protected void assertLabelInList(int column, String expectedLabel) { 
		assertLabelInCollection("list", column, expectedLabel);
	}
	
	protected void assertLabelInCollection(String collection, int column, String expectedLabel) {
		String label = getHeader(collection, column).getText().trim();
		assertEquals(expectedLabel, label);
	}
	
	protected WebElement getHeader(String collection, int column) {
		return getCell(collection, 0, column);
	}
	
	protected void moveColumn(int sourceColumn, int targetColumn) throws Exception {
		moveColumn( "list", sourceColumn, targetColumn);
	}
	
	protected void moveColumn(String collection, int sourceColumn, int targetColumn) throws Exception {
		WebElement handle = getHeader(collection, sourceColumn).findElement(By.className("xava_handle")); 
		String classTargetPoint = sourceColumn > targetColumn?"xava_handle":"mdi-close-circle"; 
		WebElement targetPoint = getHeader(collection, targetColumn).findElement(By.className(classTargetPoint));
		Actions actions = new org.openqa.selenium.interactions.Actions(driver);
		actions.dragAndDrop(handle, targetPoint).build().perform();
		wait(driver);
	}
	
	protected void removeColumn(int columnIndex) throws Exception {
		removeColumn("list", columnIndex);
	}
	
	protected void removeColumn(String collection, int columnIndex) throws Exception {
		WebElement removeButton = getHeader(collection, columnIndex).findElement(By.className("mdi-close-circle"));
		removeButton.click();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(1000));
		wait.until(ExpectedConditions.invisibilityOf(removeButton));
		wait(driver);
	}
	
	protected void assertDescriptionValue(String name, String value) { 
		assertEquals(XavaResources.getString("unexpected_value", name), value, getDescriptionValue(name));		
	}
	
	protected String getDescriptionValue(String name) {		
		WebElement input = driver.findElement(By.name(Ids.decorate("openxavatest", module, name + "__DESCRIPTION__")));
		return input.getAttribute("value");
	}
	
	protected void assertValue(String name, String value) {
		assertEquals(XavaResources.getString("unexpected_value", name), value, getValue(name));		
	}
	
	protected String getValue(String name) {
		WebElement input = driver.findElement(By.id(Ids.decorate("openxavatest", module, name)));
		return input.getAttribute("value");
	}
	
	protected String getText(String name) {
		WebElement input = driver.findElement(By.id(Ids.decorate("openxavatest", module, name)));
		return input.getText();
	}
	
	protected void setValue(String name, String value) throws Exception {
		WebElement input = driver.findElement(By.id(Ids.decorate("openxavatest", module, name)));
		input.clear();
		input.sendKeys(value);	
	}
	
	protected void assertNoErrors() {
		WebElement errors = driver.findElement(By.id("ox_openxavatest_" + module + "__errors"));
		assertEquals(XavaResources.getString("unexpected_messages", "Errors"), "", errors.getText());
	}
	
	protected void clearListCondition() throws Exception{
		driver.findElement(By.id("ox_openxavatest_" +  module + "__xava_clear_condition")).click();
		wait(driver);
	}

	protected void setConditionValue(String value, int column) {
		driver.findElement(By.id("ox_openxavatest_" + module + "__conditionValue___" + String.valueOf(column))).sendKeys(value); 		
	}
	
	protected void setConditionComparator(String value, int column) throws Exception {
		WebElement selectCondition = driver.findElement(By.id("ox_openxavatest_" + module + "__conditionComparator___" + String.valueOf(column)));
		Select select = new Select(selectCondition);
		boolean b = select.getFirstSelectedOption().getText().equals(value);
		select.selectByVisibleText(value);
		wait(driver);
		if (b) execute("List.filter");
	}
	
	protected void resetModule(WebDriver driver) throws Exception {
		driver.quit();
		WebDriver newDriver = createWebDriver("en");
		this.driver = newDriver;
		goModule(module);
	}
	
	protected void assertMessage(String expectedMessage, Log log) { 
		Collection<WebElement> messages = getDriver().findElements(By.cssSelector(".ox-messages .ox-message-box"));
		StringBuffer producedMessages = new StringBuffer();
		for (WebElement message: messages) {
			String messageText = message.getText().trim();
			if (messageText.equals(expectedMessage)) return;
			producedMessages.append(messageText);
			producedMessages.append('\n');
		}
		log.error(XavaResources.getString("messages_produced", producedMessages));
		fail(XavaResources.getString("message_not_found", expectedMessage)); 
	}
	
	protected void assertMessage(String expectedMessage) {
		List<WebElement> messages = getDriver().findElements(By.className("ox-message-box"));
		assertEquals(expectedMessage, messages.get(messages.size()-1).getText());
	}
	
	protected void assertNoMessage() {
		List<WebElement> messages = getDriver().findElements(By.className("ox-message-box"));
		assertTrue(messages.isEmpty());
	}
}
