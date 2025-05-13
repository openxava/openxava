package org.openxava.test.tests.byfeature;

import java.util.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

/**
 * tmr Quitar
 * Test for time editor functionality in OpenXava.
 * 
 * @author Javier Paniza
 */
public class TimeEditorTest extends WebDriverTestBase {
	
	public TimeEditorTest(String testName) {
		super(testName);
	}

	@Override
	protected boolean isHeadless() {
		return false;
	}

	@Override
	protected void tearDown() throws Exception {
	}
	
	/**
	 * Test for time editor with separated time part, auto-fill colon and onChange functionality.
	 */
	public void testTime_dateTimeSeparatedUseTimeEditor_separatedTimePartOnChange_autoFillColon() throws Exception {
		goModule("Event");
		execute("List.viewDetail", "row=0");
		assertValue("endTime", "1:00 PM"); 
		List<WebElement> iconElements = getDriver().findElements(By.cssSelector("i.mdi.mdi-clock-outline"));
		assertTrue(iconElements.size() == 2);
		if (!iconElements.isEmpty()) {
		    WebElement firstIconElement = iconElements.get(0);
		    firstIconElement.click();
		}
		List<WebElement> spanElements = getDriver().findElements(By.cssSelector("span.flatpickr-am-pm"));
		if (!spanElements.isEmpty()) {
		    WebElement firstSpanElement = spanElements.get(1);
		    firstSpanElement.click();
		}
		WebElement label = getDriver().findElement(By.id("ox_openxavatest_Event__label_name"));
		label.click();
		assertValue("endTime", "1:00 AM");
		List<WebElement> createDateTime = getDriver().findElements(By.id("ox_openxavatest_Event__createDate"));
		WebElement timePart = createDateTime.get(1);
		timePart.sendKeys(Keys.TAB);
		List<WebElement> messages = getDriver().findElements(By.cssSelector(".ox-messages .ox-message-box"));
		assertTrue(messages.isEmpty());
		setValue("endTime", "111 PM");
		WebElement endTime = getDriver().findElement(By.id("ox_openxavatest_Event__endTime"));
		endTime.sendKeys(Keys.TAB);
		assertValue("endTime","1:11 PM");
		setValue("endTime", "100 PM");
		endTime.sendKeys(Keys.TAB);
		messages = getDriver().findElements(By.cssSelector(".ox-messages .ox-message-box"));
		assertTrue(messages.isEmpty());
		execute("CRUD.save");
		assertNoErrors(); // tmr Mover a test original, Otro bug no era capaz de formatear 1:00 PM con Java 21 en inglés
				 
		// tmr Hemos cambiado el test de abajo
		changeLanguage("zh-CN");
		goModule("Event");
		execute("List.viewDetail", "row=0");
		assertValue("endTime", "PM1:00"); // tmr Esto podría ser otro bug. Formateo de a. m. en Java 21, aunque creo que tiene que ver con el locale del servidor
		endTime = getDriver().findElement(By.id("ox_openxavatest_Event__endTime"));
		endTime.sendKeys(Keys.TAB);
		// tmr Al arreglar lo de abajo también se arregla que el popup rompe las horas (podría redactarse en el mismo bug)
		// tmr Lo de abajo también falla con Java 11, pasaba porque el test estaba mal
		// TMR ME QUEDÉ PROBANDO LO DE ABAJO EN timeCalendarEditor.js EN target
		assertValue("endTime", "PM1:00"); // It fails in Java 21: https://openxava.org/xavaprojects/o/OpenXava/m/Issue?detail=ff8080819581a7c6019594cccba40021
		openTimeCalendar(0);
		changeAmPm(1);
		assertValue("endTime", "AM1:00");
		openTimeCalendar(0);
		changeAmPm(1);
		assertValue("endTime", "PM1:00");
		execute("CRUD.save"); 		
		assertNoErrors();		
	}
	
	/**
	 * Opens the time calendar for the specified element index.
	 */
	private void openTimeCalendar(int i) throws InterruptedException {
		List<WebElement> iconElements = getDriver().findElements(By.cssSelector("i.mdi.mdi-clock-outline"));
		if (!iconElements.isEmpty()) {
		    WebElement timeIcon = iconElements.get(i);
		    timeIcon.click();
		}
		Thread.sleep(500);
	}
	
	/**
	 * Changes the AM/PM setting for the specified element index.
	 */
	private void changeAmPm(int i) throws InterruptedException {
		List<WebElement> iconElements = getDriver().findElements(By.className("flatpickr-am-pm"));
		if (!iconElements.isEmpty()) {
		    WebElement timeIcon = iconElements.get(i);
		    timeIcon.click();
		}
		Thread.sleep(500);
	}
}
