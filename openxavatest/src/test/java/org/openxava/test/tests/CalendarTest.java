package org.openxava.test.tests;

import java.text.*;
import java.time.*;
import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.support.ui.*;

public class CalendarTest extends WebDriverTestBase {

	private WebDriver driver;

	public void setUp() throws Exception {
		//setHeadless(true);
		driver = createWebDriver();
	}
/*
	public void testNavigation() throws Exception {
		forTestAddEventAndVerify();
		forTestConditionsAndFilter();
		forTestAnyNameAsDateProperty();
		forTestMultipleDateAndFirstDateAsEventStart();  
		forTestFilterPerformance();
		forTestMore();
		forTestCreateDateWithTimeInWeekAndDailyView_tooltip();
	}*/

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
	//terminado
	private void testNavigationInDateCalendarAndDateTimeCalendar() throws Exception {
		goTo(driver, "http://localhost:8080/openxavatest/m/Appointment");
		moveToCalendarView(driver);
		moveToTimeGridWeek(driver);
		moveToListView(driver);
		goTo(driver, "http://localhost:8080/openxavatest/m/Event");
		moveToCalendarView(driver);
		boolean weekButton = driver.findElements(By.cssSelector("button.fc-timeGridWeek-button")).isEmpty();
		assertTrue(weekButton);
		moveToListView(driver);
	}
	
	private void testAnyNameAsDateProperty() throws Exception {
		goTo(driver, "http://localhost:8080/openxavatest/m/UserWithBirthday");
		try {
			execute(driver, "UserWithBirthday", "Mode.list");
		} catch (NoSuchElementException e) {
		}
		moveToListView(driver);
		moveToCalendarView(driver);
		moveToListView(driver);
	}
	//terminado
	private void testCreateEventPrevCurrentNextMonth() throws Exception {
		goTo(driver, "http://localhost:8080/openxavatest/m/Invoice");
		moveToCalendarView(driver);
		prevOnCalendar();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		List<Date> dates = setDates();
		for (int i = 0; i < dates.size(); i++) {
			if (i == 2) {
				nextOnCalendar();
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
		prevOnCalendar();
		verifyPrevInvoiceEvent();
		moveToListView(driver);
	}
	//terminado
	private void testConditionsAndFilter() throws Exception {
		goTo(driver, "http://localhost:8080/openxavatest/m/InvoiceCalendar");
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
	
	private void testFilterPerformance() throws Exception {
		//testing default filter defined by user and month filter defined by calendar
		goTo(driver, "http://localhost:8080/openxavatest/m/EventWithFilter");
		moveToListView(driver);
		long ini = System.currentTimeMillis();
		moveToCalendarView(driver);
		long takes = System.currentTimeMillis() - ini;
		// with CompositeFilter it takes no more than 1500, without it takes more than
		// 4000
		assertTrue(takes < 3000);
		moveToListView(driver);
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

	private void createInvoice(int invoiceNUmber) throws Exception {
//		WebElement inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_Invoice__number"));
//		int invoiceNumber = (10 + invoiceNUmber);
//		inputInvoiceNumber.sendKeys(String.valueOf(invoiceNumber));
		insertValueToInput(driver, "ox_openxavatest_Invoice__number", String.valueOf(10 + invoiceNUmber), false);
//		WebElement inputCustomerNumber = driver.findElement(By.id("ox_openxavatest_Invoice__customer___number"));
//		inputCustomerNumber.sendKeys("1");
		insertValueToInput(driver, "ox_openxavatest_Invoice__customer___number", "1", false);
//		WebElement section2Child = driver
//				.findElement(By.id("ox_openxavatest_Invoice__label_xava_view_section2_sectionName"));
//		WebElement section2Parent = section2Child.findElement(By.xpath(".."));
//		section2Parent.click();
//		wait(driver);
		clickOnSectionWithChildSpanId(driver, "ox_openxavatest_Invoice__label_xava_view_section2_sectionName");
//		WebElement inputVAT = driver.findElement(By.id("ox_openxavatest_Invoice__vatPercentage"));
//		inputVAT.sendKeys("3");
		insertValueToInput(driver, "ox_openxavatest_Invoice__vatPercentage", "3", false);
		execute(driver, "Invoice", "CRUD.save");
		execute(driver, "Invoice", "Mode.list");
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
		execute(driver, "Invoice", "Mode.list");
		waitCalendarEvent(driver);
	}

	private void setInvoiceCondition(String module) throws InterruptedException {
		WebElement inputInvoiceNumber = driver.findElement(By.id("ox_openxavatest_" + module + "__conditionValue___1"));
		inputInvoiceNumber.sendKeys("12");
		//insertValueToInput(driver, "ox_openxavatest_" + module + "__conditionValue___1", "12", false);
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("arguments[0].focus();", inputInvoiceNumber);
		WebElement selectInvoiceNumberCondition = driver
				.findElement(By.id("ox_openxavatest_" + module + "__conditionComparator___1"));
		Select select = new Select(selectInvoiceNumberCondition);
		select.selectByVisibleText("=");
		WebElement filterAction = driver.findElement(By.id("ox_openxavatest_" + module + "__List___filter"));
		filterAction.click();
	}

	private void verifyConditionEvents(String time, boolean isExist) {
		WebElement currentMonthEvent = null;
		try {
			currentMonthEvent = driver.findElement(By.cssSelector(
					"a.fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end.fc-event-" + time
							+ ".fc-daygrid-event.fc-daygrid-dot-event"));
		} catch (NoSuchElementException e) {
		}
		assert isExist ? currentMonthEvent != null : currentMonthEvent == null;

	}

	private void deteleEvents() throws Exception {
		WebElement clearFilterAction = driver
				.findElement(By.cssSelector("td.ox-list-subheader a:has(i.mdi.mdi-eraser)"));
		clearFilterAction.click();
		wait(driver);
		clickOnButtonWithId(driver, "ox_openxavatest_InvoiceCalendar__xava_clear_condition");
		for (int i = 0; i < 3; i++) {
		    execute(driver, "InvoiceCalendar", "CRUD.deleteRow", "row=0");
		}
	}







	private void forTestMultipleDateAndFirstDateAsEventStart() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Event");
		wait(driver);
		acceptInDialogJS(driver);
		moveToCalendarView(driver);
		List<Date> dates = setDates();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = dateFormat.format(dates.get(1));

		WebElement day = driver.findElement(By.xpath(
				"//div[contains(@class,'fc-daygrid-day-frame') and ancestor::td[@data-date='" + dateString + "']]"));
		day.click();
		wait(driver);
		
		WebElement startDate = driver.findElement(By.id("ox_openxavatest_Event__startDate"));
		blur(driver, startDate);
		//sleep needed after blur
		Thread.sleep(500);
		List<WebElement> iconElements = driver.findElements(By.cssSelector("i.mdi.mdi-calendar"));
		if (!iconElements.isEmpty()) {
			WebElement firstIconElement = iconElements.get(1);
			firstIconElement.click();
		}

		List<WebElement> spanElements = driver
			.findElements(By.xpath("//div[@class='dayContainer']//span[@class='flatpickr-day ' and text()='2']"));
		
		if (!spanElements.isEmpty()) {
			WebElement spanElement = spanElements.get(1); 
			spanElement.click(); 
		}

		wait(driver);
		execute(driver, "Event", "CRUD.save");
		execute(driver, "Event", "Mode.list");
		waitCalendarEvent(driver);

		List<WebElement> events = driver
				.findElements(By.xpath("//div[contains(@class,'fc-daygrid-event-harness') and ancestor::td[@data-date='"
						+ dateString + "']]"));
		assertTrue(!events.isEmpty());

		moveToListView(driver);
		List<WebElement> elements = driver.findElements(
				By.xpath("//a[contains(@class, 'ox-image-link') and .//i[contains(@class, 'mdi-delete')]]"));
		elements.get(1).click();
		acceptInDialogJS(driver);
	}


	
	private void forTestMore() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Hound");
		wait(driver);
		acceptInDialogJS(driver);
		execute(driver, "Hound", "CRUD.new");
		for (int i = 0; i < 6; i++) {
			insertValueToInput(driver, "ox_openxavatest_Hound__name", String.valueOf(i), true);
			execute(driver, "Hound", "CRUD.save");
		}
		execute(driver, "Hound", "Mode.list");
		waitCalendarEvent(driver);
		moveToCalendarView(driver);
		WebElement linkElement = driver.findElement(By.cssSelector("a.fc-daygrid-more-link.fc-more-link"));
		assertNotNull(linkElement);
		
		moveToListView(driver);

		for (int i = 0; i < 6; i++) {
			WebElement checkboxElement = driver.findElement(By.xpath("//input[@name='ox_openxavatest_Hound__xava_selected' and @value='selected:" + i + "']"));
			checkboxElement.click();
		}
		execute(driver, "Hound", "CRUD.deleteSelected");
	}

	private void forTestCreateDateWithTimeInWeekAndDailyView_tooltip() throws Exception {
		driver.get("http://localhost:8080/openxavatest/m/Appointment");
		wait(driver);
		acceptInDialogJS(driver);
		moveToCalendarView(driver);
		moveToTimeGridWeek(driver);
		
        WebElement dayTimeCell = driver.findElement(By.cssSelector("tr:nth-child(6) > .fc-timegrid-slot-lane"));
        dayTimeCell.click();
        wait(driver);
        
        WebElement dateTime = driver.findElement(By.id("ox_openxavatest_Appointment__time"));
        String dateTimeInput = dateTime.getAttribute("value");
        assertTrue(dateTimeInput.contains("2:30"));
        insertValueToInput(driver, "ox_openxavatest_Appointment__description", "A", false);
        execute(driver, "Appointment", "CRUD.save");
        execute(driver, "Appointment", "Mode.list");
        waitCalendarEvent(driver);

        //tooltip
		WebElement monthEvent = driver.findElement(By.cssSelector(".fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end"));
		Actions builder = new Actions(driver);
        builder.moveToElement(monthEvent).perform();
        Thread.sleep(500);
		WebElement tooltip = driver.findElement(By.cssSelector(".fc-event-tooltip"));
		assertEquals("A", tooltip.getText());
        
        moveToTimeGridWeek(driver);
        WebElement event = driver.findElement(By.cssSelector(".fc-event.fc-event-draggable.fc-event-resizable.fc-event-start.fc-event-end"));
        event.click();
        wait(driver);
        WebElement dateTime2 = driver.findElement(By.id("ox_openxavatest_Appointment__time"));
        String dateTimeInput2 = dateTime2.getAttribute("value");
        assertTrue(dateTimeInput2.contains("2:30"));
        execute(driver, "Appointment", "CRUD.delete");
        execute(driver, "Appointment", "Mode.list");
        waitCalendarEvent(driver);
        // used to verify existence of daily view
		WebElement dayButton = driver.findElement(By.cssSelector("button.fc-timeGridDay-button"));
		dayButton.click();
		waitCalendarEvent(driver);
		moveToListView(driver);
		acceptInDialogJS(driver);
	}
	
//	private boolean isMonthWeekDayViewPresent(WebDriver driver) {
//		int monthButton = driver.findElements(By.className("fc-timeGridWeek-button")).size();
//        int weekButton = driver.findElements(By.className("fc-timeGridDay-button")).size();
//        int dayButton = driver.findElements(By.className("fc-dayGridMonth-button")).size();
//        boolean b = (monthButton == 1 && weekButton == 1 && dayButton == 1) ? true : false;
//        
//        return b;
//	}
	
	protected void waitCalendarEvent(WebDriver driver) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(10000));
		try {
			wait.until(ExpectedConditions.jsReturnsValue("return calendarEditor.requesting === false;"));
			Thread.sleep(500);
		} catch (Exception ex) {
		}
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
	
	protected void moveToCalendarView(WebDriver driver) throws Exception {
		WebElement tabCalendar = driver.findElement(By.cssSelector(".mdi.mdi-calendar"));
		WebElement tabCalendarParent = tabCalendar.findElement(By.xpath(".."));
		String title = tabCalendarParent.getAttribute("class");
		if (!(title != null && title.contains("ox-selected-list-format"))) {
			tabCalendar.click();
		}
		wait(driver);
		waitCalendarEvent(driver);
	}
	
	protected void moveToTimeGridWeek(WebDriver driver) throws Exception {
		WebElement weekButton = driver.findElement(By.cssSelector("button.fc-timeGridWeek-button"));
		weekButton.click();
		waitCalendarEvent(driver);
	}

	
}
