package org.openxava.test.tests;

import java.text.*;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.ui.*;

public class CalendarTest extends WebDriverTestBase {

	private WebDriver driver;

	public void setUp() throws Exception {
		driver = createWebDriver();
	}

	public void testNavigation() throws Exception {
		forTestAddEventAndVerify();
		forTestConditions();
		forTestAnyNameAsDateProperty();
		forTestMultipleDateAndFirstDateAsEventStart(); 
	}

	public void tearDown() throws Exception {
		driver.quit();
	}

	private void nextOnCalendar() throws Exception {
		WebElement next = driver.findElement(By.cssSelector(".fc-icon.fc-icon-chevron-right"));
		next.click();
		waitCalendarEvent(driver);
	}

	private void prevOnCalendar() throws Exception {
		WebElement prev = driver.findElement(By.cssSelector(".fc-icon.fc-icon-chevron-left"));
		prev.click();
		waitCalendarEvent(driver);
	}

	private List<Date> setDates() {
		List<Date> dates = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date currentMonth = calendar.getTime();
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date previousMonth = calendar.getTime();
		calendar.add(Calendar.MONTH, 2);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		Date nextMonth = calendar.getTime();
		dates.add(previousMonth);
		dates.add(currentMonth);
		dates.add(nextMonth);

		return dates;
	}

	private void createInvoiceEventPrevCurrentNextMonth() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		List<Date> dates = setDates();
		for (int i = 0; i < dates.size(); i++) {
			if (i == 2) {
				nextOnCalendar();
				waitCalendarEvent(driver);
				wait(driver);
			}
			String dateString = dateFormat.format(dates.get(i));
			WebElement day = driver
					.findElement(By.xpath("//div[contains(@class,'fc-daygrid-day-frame') and ancestor::td[@data-date='"
							+ dateString + "']]"));
			day.click();
			wait(driver);
			createInvoice(i);
		}
	}
	
	private void createInvoice(int invoiceNUmber) throws Exception {
		WebElement inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_Invoice__number"));
		int invoiceNumber = (10 + invoiceNUmber);
		inputInvoiceNumber.sendKeys(String.valueOf(invoiceNumber));			
		WebElement inputCustomerNumber = driver.findElement(By.id("ox_openxavatest_Invoice__customer___number"));
		inputCustomerNumber.sendKeys("1");
		WebElement section2Child = driver
				.findElement(By.id("ox_openxavatest_Invoice__label_xava_view_section2_sectionName"));
		WebElement section2Parent = section2Child.findElement(By.xpath(".."));
		section2Parent.click();
		wait(driver);
		WebElement inputVAT = driver.findElement(By.id("ox_openxavatest_Invoice__vatPercentage"));
		inputVAT.sendKeys("3");
		saveFromDetailView(driver, "Invoice");
		goToListFromDetailView(driver, "Invoice");
		waitCalendarEvent(driver);
	}

	private void verifyPrevInvoiceEvent() throws Exception {
		WebElement currentMonthEvent = driver.findElement(By.cssSelector(
				"a.fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end.fc-event-past.fc-daygrid-event.fc-daygrid-dot-event"));
		currentMonthEvent.click();
		wait(driver);
		WebElement invoiceNumber = driver.findElement(By.id("ox_openxavatest_Invoice__number"));
		assertEquals("10", invoiceNumber.getAttribute("value"));
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		WebElement invoiceYear = driver.findElement(By.id("ox_openxavatest_Invoice__year"));
		assertEquals(String.valueOf(year), invoiceYear.getAttribute("value"));
		goToListFromDetailView(driver, "Invoice");
		waitCalendarEvent(driver);
	}
	
	
	private void setInvoiceCondition(String module) throws InterruptedException {
		WebElement inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_" + module + "__conditionValue___1"));
		inputInvoiceNumber.sendKeys("12");
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].focus();", inputInvoiceNumber);
		WebElement selectInvoiceNumberCondition = driver.findElement(By.id("ox_openxavatest_" + module + "__conditionComparator___1"));
		Select select = new Select(selectInvoiceNumberCondition);
		select.selectByVisibleText("=");
		WebElement filterAction = driver.findElement(By.id("ox_openxavatest_" + module + "__List___filter"));
		filterAction.click();
	}
	
	private void verifyConditionEvents(String time, boolean isExist) {
		WebElement currentMonthEvent = null;
		try {
		    currentMonthEvent = driver.findElement(By.cssSelector(
		            "a.fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end.fc-event-" + time + ".fc-daygrid-event.fc-daygrid-dot-event"));
		} catch (NoSuchElementException e) {}	
		assert isExist ? currentMonthEvent != null : currentMonthEvent == null;

	}
	
	private void deteleEvents() throws Exception {
		WebElement clearFilterAction = driver.findElement(By.cssSelector("td.ox-list-subheader a:has(i.mdi.mdi-eraser)"));
		clearFilterAction.click();
		wait(driver);
		for (int i = 0; i < 3; i ++) {
			WebElement element = driver.findElement(By.xpath("//a[contains(@class, 'ox-image-link') and .//i[contains(@class, 'mdi-delete')]]"));
			element.click();
			acceptInDialogJS(driver);
		}
	}
	
	private void forTestAnyNameAsDateProperty() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/UserWithBirthday");
		wait(driver);
		acceptInDialogJS(driver);
		try {
			goToListFromDetailView(driver, "UserWithBirthday");
		} catch (NoSuchElementException e) {
		}
		moveToListView(driver);
		moveToCalendarView(driver);
		moveToListView(driver);
	}
	
	private void forTestAddEventAndVerify() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Invoice");
		wait(driver);
		acceptInDialogJS(driver);
		moveToCalendarView(driver);
		prevOnCalendar();
		createInvoiceEventPrevCurrentNextMonth();
		prevOnCalendar();
		verifyPrevInvoiceEvent();
	}
	
	private void forTestConditions() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/InvoiceCalendar");
		wait(driver);
		moveToListView(driver);
		setInvoiceCondition("InvoiceCalendar");
		wait(driver);
		moveToCalendarView(driver);
		prevOnCalendar();
		verifyConditionEvents("past", false);
		nextOnCalendar();
		nextOnCalendar();
		verifyConditionEvents("future", true);
		moveToListView(driver);
		deteleEvents();
	}
	
	private void forTestMultipleDateAndFirstDateAsEventStart() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Event");
		wait(driver);
		acceptInDialogJS(driver);
		moveToCalendarView(driver);
		List<Date> dates = setDates();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = dateFormat.format(dates.get(1));
		
		WebElement day = driver
				.findElement(By.xpath("//div[contains(@class,'fc-daygrid-day-frame') and ancestor::td[@data-date='"
						+ dateString + "']]"));
		day.click();
		wait(driver);
		List<WebElement> iconElements = driver.findElements(By.cssSelector("i.mdi.mdi-calendar"));
		if (!iconElements.isEmpty()) {
		    WebElement firstIconElement = iconElements.get(1);
		    firstIconElement.click();
		}
		
		List<WebElement> spanElements = driver.findElements(By.xpath("//div[@class='dayContainer']//span[@class='flatpickr-day ' and text()='2']"));
		if (!spanElements.isEmpty()) {
		    WebElement spanElement = spanElements.get(1);
		    spanElement.click(); // It fails in Windows 7
		}
		wait(driver);
		saveFromDetailView(driver, "Event");
		goToListFromDetailView(driver, "Event");
		waitCalendarEvent(driver);
		
		List<WebElement> events = driver.findElements(By.xpath("//div[contains(@class,'fc-daygrid-event-harness') and ancestor::td[@data-date='"
                + dateString + "']]"));
		assertTrue(!events.isEmpty());
		
		moveToListView(driver);
		List<WebElement> elements = driver.findElements(By.xpath("//a[contains(@class, 'ox-image-link') and .//i[contains(@class, 'mdi-delete')]]"));
		elements.get(1).click();
		acceptInDialogJS(driver);
	}
	
}
