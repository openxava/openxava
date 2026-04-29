package com.yourcompany.yourapp.tests;

import java.time.*;
import java.time.format.*;

import org.openxava.tests.*;

public class PlanTest extends ModuleTestBase {

	public PlanTest(String nameTest) {
		super(nameTest, "yourapp", "Plan");
	}
	
	public void testCreateNewPlan() throws Exception {
		login("admin", "admin");
		assertListRowCount(2);
		execute("CRUD.new");
		
		String [][] workers = {
			{ "", "" },
			{ "2c94f081900875e80190088fd8f60004", "Javi" },
			{ "2c94f081900875e8019008901a180005", "Peter" }
		};
		assertValidValues("worker.id", workers);
		setValue("worker.id", "2c94f081900875e8019008901a180005"); // Peter 
		
		String [][] periods = {
			{ "", "" },
			{ "2c94f081900875e80190089394730006", "2024.10" },
			{ "2c94f081900875e801900893a5e90007", "2024.11" }
		};
		assertValidValues("period.id", periods);
		setValue("period.id", "2c94f081900875e801900893a5e90007"); // 2024.11 
		
		execute("Collection.new", "viewObject=xava_view_issues");
		setValue("title", "The first step of my big plan");
		setValue("type.id", "2c94f081900875e801900896f25b0008"); // Bug 
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("issues", 1);
		
		execute("Mode.list");
		assertListRowCount(3);
		assertValueInList(1, 0, "Peter");
		assertValueInList(1, 1, "2024.11");

		execute("List.viewDetail", "row=0");
		execute("Navigation.next");
		assertNoErrors(); // To test a bug with plannedFor data formatter when navigating

		assertValue("worker.id", "2c94f081900875e8019008901a180005"); // Peter
		assertValue("period.id", "2c94f081900875e801900893a5e90007"); // 2024.11		
		assertCollectionRowCount("issues", 1);
		assertValueInCollection("issues", 0, 0, "The first step of my big plan");
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_issues");
		execute("CRUD.delete");
		assertNoErrors();
		
		changeModule("Issue");
		assertListRowCount(1);
		assertValueInList(0, 0, "The first step of my big plan");
		assertValueInList(0, 1, "Bug");
		checkAll();
		execute("CRUD.deleteSelected");
		assertNoErrors();
	}

	
	public void testDeadlineDateListFormatter() throws Exception { 
		login("admin", "admin");
		
		// Get the first plan
		execute("List.viewDetail", "row=0");
		
		// Calculate working day dates
		LocalDate today = LocalDate.now();
		LocalDate nextWorkingDay = getNextWorkingDay(today);
		LocalDate secondNextWorkingDay = getNextWorkingDay(nextWorkingDay);
		LocalDate otherDate = LocalDate.of(2025, 3, 15);
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/yyyy");
		
		// Add issue for today
		execute("Collection.new", "viewObject=xava_view_issues");
		setValue("title", "Issue for today");
		setValue("type.id", "2c94f081900875e801900896f25b0008"); // Bug
		setValue("plannedFor", today.format(formatter));
		execute("Collection.save");
		assertNoErrors();
		
		// Add issue for next working day (tomorrow)
		execute("Collection.new", "viewObject=xava_view_issues");
		setValue("title", "Issue for tomorrow");
		setValue("type.id", "2c94f081900875e801900896f25b0008"); // Bug
		setValue("plannedFor", nextWorkingDay.format(formatter));
		execute("Collection.save");
		assertNoErrors();
		
		// Add issue for second next working day (day after tomorrow)
		execute("Collection.new", "viewObject=xava_view_issues");
		setValue("title", "Issue for day after tomorrow");
		setValue("type.id", "2c94f081900875e801900896f25b0008"); // Bug
		setValue("plannedFor", secondNextWorkingDay.format(formatter));
		execute("Collection.save");
		assertNoErrors();
		
		// Add issue for other date
		execute("Collection.new", "viewObject=xava_view_issues");
		setValue("title", "Issue for other date");
		setValue("type.id", "2c94f081900875e801900896f25b0008"); // Bug
		setValue("plannedFor", otherDate.format(formatter));
		execute("Collection.save");
		assertNoErrors();
		
		// Verify the collection has 4 issues
		assertCollectionRowCount("issues", 4);
		
		// Get the HTML and verify the CSS classes are applied correctly to the right dates
		String html = getHtml();
		
		// Format dates as they appear in the HTML (M/d/yyyy format for English locale)
		DateTimeFormatter htmlFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
		String todayFormatted = today.format(htmlFormatter);
		String tomorrowFormatted = nextWorkingDay.format(htmlFormatter);
		String dayAfterTomorrowFormatted = secondNextWorkingDay.format(htmlFormatter);
		String otherDateFormatted = otherDate.format(htmlFormatter);
		
		// Verify today's date has the 'deadline-today' class
		assertTrue("Today's date (" + todayFormatted + ") should have 'deadline-today' class", 
			html.contains("<span class=\"deadline-today\">" + todayFormatted + "</span>") || 
			html.contains("<span class='deadline-today'>" + todayFormatted + "</span>"));
		
		// Verify tomorrow's date has the 'deadline-tomorrow' class
		assertTrue("Tomorrow's date (" + tomorrowFormatted + ") should have 'deadline-tomorrow' class", 
			html.contains("<span class=\"deadline-tomorrow\">" + tomorrowFormatted + "</span>") || 
			html.contains("<span class='deadline-tomorrow'>" + tomorrowFormatted + "</span>"));
		
		// Verify day after tomorrow's date has the 'deadline-day-after-tomorrow' class
		assertTrue("Day after tomorrow's date (" + dayAfterTomorrowFormatted + ") should have 'deadline-day-after-tomorrow' class", 
			html.contains("<span class=\"deadline-day-after-tomorrow\">" + dayAfterTomorrowFormatted + "</span>") || 
			html.contains("<span class='deadline-day-after-tomorrow'>" + dayAfterTomorrowFormatted + "</span>"));

		// Verify other date has no special class (no span wrapper)
		assertFalse("Other date (" + otherDateFormatted + ") should not have any deadline class", 
			html.contains("<span class=\"deadline-today\">" + otherDateFormatted + "</span>") ||
			html.contains("<span class=\"deadline-tomorrow\">" + otherDateFormatted + "</span>") ||
			html.contains("<span class=\"deadline-day-after-tomorrow\">" + otherDateFormatted + "</span>"));

		// Clean up: remove all issues
		checkAllCollection("issues");
		execute("Collection.deleteSelected", "viewObject=xava_view_issues");
		
		assertCollectionRowCount("issues", 0);
	}
	
	private LocalDate getNextWorkingDay(LocalDate date) {
		LocalDate nextDay = date.plusDays(1);
		while (nextDay.getDayOfWeek() == DayOfWeek.SATURDAY || 
			   nextDay.getDayOfWeek() == DayOfWeek.SUNDAY) {
			nextDay = nextDay.plusDays(1);
		}
		return nextDay;
	}

}
