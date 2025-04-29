package com.yourcompany.yourapp.tests;

import java.time.*;
import java.util.*;

import javax.persistence.*;

import org.openqa.selenium.*;
import org.openxava.jpa.*;
import org.openxava.util.*;
import com.yourcompany.yourapp.model.*;
import com.yourcompany.yourapp.model.Period;

/**
 * Test for the My Calendar module using Selenium WebDriver.
 */
public class MyCalendarTest extends WebDriverTestBase {
	
	private int year = Dates.getYear(new Date());
	private int month = Dates.getMonth(new Date());
	
	protected void setUp() throws Exception {
		super.setUp();
		XPersistence.setPersistenceUnit("junit");
	}
		
	public void testCreateIssue() throws Exception {
		goModule("MyCalendar");
		login("javi", "javi");

		// Test creating an issue without a plan
		assertNewWithNoPlan();		
		
		// Create a plan for the current month
		createPlanForMonth(year, month);		
		
		// Test creating an issue without default status and type
		assertNewWithNoDefaultForStatusAndType();
		
		// Test clicking on a day in the calendar
		clickOnDay();
		assertNoErrors();
		assertValue("plannedFor", month + "/15/" + year);
		assertDescriptionValue("type.id", "Task");
		assertDescriptionValue("status.id", "Planned");
		assertDescriptionValue("assignedTo.id", "Javi " + year + "." + month);	
		
		// Test validation error when title is missing
		execute("MyCalendar.save"); 
		assertError("Value for Title in Issue is required");

		// Test successful creation of an issue
		setValue("title", "JUnit incident from My calendar");
		execute("MyCalendar.save"); 
		assertNoErrors(); 
		
		// Verify the issue appears in the calendar
		assertDayText(15, "JUnit incident from My calendar"); 
		
		// Test creating a new issue with default values
		execute("MyCalendar.new");
		assertNoErrors();
		assertValue("plannedFor", month + "/" + Dates.getDay(new Date()) + "/" + year);
		assertDescriptionValue("type.id", "Task");
		assertDescriptionValue("status.id", "Planned");
		assertDescriptionValue("assignedTo.id", "Javi " + year + "." + month);	
		
		// Test issue visibility for different users
		logout();
		login("juan", "juan");
		goModule("MyCalendar");
		assertDayText(15, "");
		
		// Clean up test data
		deleteData("JUnit incident from My calendar");
	}

	private void assertNewWithNoDefaultForStatusAndType() throws Exception {
		// Find and disable default issue status for My Calendar
		IssueStatus issueStatus = IssueStatus.findTheDefaultOneForMyCalendar();
		issueStatus.setUseAsDefaultValueForMyCalendar(false);
		String issueStatusId = issueStatus.getId();
		
		// Find and disable default issue type for My Calendar
		IssueType issueType = IssueType.findTheDefaultOneForMyCalendar();
		issueType.setUseAsDefaultValueForMyCalendar(false);
		String issueTypeId = issueType.getId();
		
		XPersistence.commit();
		
		// Click on a day and verify no defaults are used
		clickOnDay();
		
		assertNoErrors();
		assertValue("plannedFor", month + "/15/" + year);
		assertDescriptionValue("type.id", "");
		assertDescriptionValue("status.id", "Pending");  
		assertDescriptionValue("assignedTo.id", "Javi " + year + "." + month);
		
		// Restore default values
		IssueStatus.findById(issueStatusId).setUseAsDefaultValueForMyCalendar(true);
		IssueType.findById(issueTypeId).setUseAsDefaultValueForMyCalendar(true);
		XPersistence.commit();
		
		execute("Mode.list");
	}

	private void assertNewWithNoPlan() throws Exception {
		// Click on a day when there's no plan
		clickOnDay();
		assertError("There is no plan for javi on the date " + formattedDate() + ". Create one and set it in the Assigned to field");
		assertValue("plannedFor", month + "/15/" + year);
		assertDescriptionValue("type.id", "Task");
		assertDescriptionValue("status.id", "Planned");
		assertDescriptionValue("assignedTo.id", "");
		execute("Mode.list");
	}
	
	private String formattedDate() {
		return String.format("%d-%02d-15", year, month);
	}

	private void clickOnDay() throws Exception {
		// Find and click on a day element in the calendar
		WebElement dayElement = getDayElement();
		dayElement.click();
		wait(getDriver());
	}
	
	private void assertDayText(int day, String expectedText) throws Exception {
		// Verify the text displayed on a calendar day
		WebElement dayElement = getDayElement();
		String expectedDayContent = Is.emptyString(expectedText)?Integer.toString(day): day + "\n" + expectedText; 
		assertEquals(expectedDayContent, dayElement.getText());
	}
	
	private WebElement getDayElement() {
		// Get the day element from the calendar
		String date = formattedDate();
		WebElement dayElement = getDriver().findElement(By.cssSelector("td[data-date='" + date + "']")); 
		return dayElement;
	}	

	private void createPlanForMonth(int year, int month) throws Exception {
		// Create a period for the month
		Period period = new Period();
		period.setName(year + "." + month);
		period.setStartDate(LocalDate.of(year, month, 1));
		period.setEndDate(YearMonth.now().atEndOfMonth()); 
		
		// Create a plan for the worker
		Plan plan = new Plan();
		plan.setWorker(Worker.findById("4028808d7eea19fe017eea56ed120018")); // Javi
		plan.setPeriod(period);
		
		// Persist the entities
		XPersistence.getManager().persist(period);
		XPersistence.getManager().persist(plan);
		
		XPersistence.commit();
	}
	
	private void deleteData(String issueTitle) {
		// Delete the issue and related entities
		Issue issue = Issue.findByTitle(issueTitle);
		EntityManager em = XPersistence.getManager();
		Plan plan = issue.getAssignedTo();
		em.remove(issue);
		em.remove(plan);
		em.remove(plan.getPeriod());
		XPersistence.commit();
	}
}
