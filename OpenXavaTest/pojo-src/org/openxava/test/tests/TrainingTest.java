package org.openxava.test.tests;

import java.text.*;

import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;



/**
 * 
 * @author Javier Paniza
 */

public class TrainingTest extends ModuleTestBase {
	
	public TrainingTest(String testName) {
		super(testName, "Training");		
	}
	
	public void testElementCollection() throws Exception { 
		assertDateEditor(); 
		setValue("description", "JUNIT TRAINING");
		assertCollectionRowCount("sessions", 0);
		assertValueInCollection("sessions", 0, "description", "");  
		assertValueInCollection("sessions", 0, "kms", "");
		assertValueInCollection("sessions", 0, "date", "");				
		execute("CRUD.save");
		assertError("It's required at least 1 element in Sessions of Training");
		assertErrorsCount(1);
		String currentDate = DateFormat.getDateInstance(DateFormat.SHORT).format(new java.util.Date()); 
		assertValueInCollection("sessions", 0, "date", "");  
		setValueInCollection("sessions", 0, "description", "RUNNING IN THE STREET");
		assertValueInCollection("sessions", 0, "date", currentDate);  
		assertCollectionRowCount("sessions", 1);
		assertValueInCollection("sessions", 1, "description", "");  
		assertValueInCollection("sessions", 1, "kms", "");
		assertValueInCollection("sessions", 1, "date", "");		
		setValueInCollection("sessions", 0, "kms", "5");
		assertCollectionRowCount("sessions", 1);
		assertCollectionRowCount("sessions", 1);
		assertValueInCollection("sessions", 1, "date", ""); 
		setValueInCollection("sessions", 1, "description", "CORRIENDO EN LA CALLE");
		assertValueInCollection("sessions", 1, "date", currentDate);  
		assertCollectionRowCount("sessions", 2);
		assertValueInCollection("sessions", 2, "description", ""); 
		assertValueInCollection("sessions", 2, "kms", "");
		assertValueInCollection("sessions", 2, "date", "");
		setValueInCollection("sessions", 1, "kms", "7");
		setValueInCollection("sessions", 1, 2, "2/1/14");
		setValueInCollection("sessions", 2, "description", "WALKING"); // The third row is generated
		assertCollectionRowCount("sessions", 3);
		assertValueInCollection("sessions", 3, "description", ""); 
		assertValueInCollection("sessions", 3, "kms", "");
		assertValueInCollection("sessions", 3, "date", "");		
		setValueInCollection("sessions", 2, "kms", "2");
		setValueInCollection("sessions", 2, "date", "3/1/14");
		assertCollectionRowCount("sessions", 3);
		execute("CRUD.save");
		assertValue("description", "");
		assertCollectionRowCount("sessions", 0);
		assertValueInCollection("sessions", 0, "description", ""); 
		assertValueInCollection("sessions", 0, "kms", "");
		assertValueInCollection("sessions", 0, "date", "");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("description", "JUNIT TRAINING");
		assertCollectionRowCount("sessions", 3);
		assertValueInCollection("sessions", 0, "description", "RUNNING IN THE STREET"); 
		assertValueInCollection("sessions", 0, 1, "5");
		assertValueInCollection("sessions", 0, "date", currentDate);  
		assertValueInCollection("sessions", 1, "description", "CORRIENDO EN LA CALLE");
		assertValueInCollection("sessions", 1, "kms", "7");
		assertValueInCollection("sessions", 1, "date", "2/1/14");
		assertValueInCollection("sessions", 2, "description", "WALKING"); 
		assertValueInCollection("sessions", 2, "kms", "2");
		assertValueInCollection("sessions", 2, "date", "3/1/14");		
		assertValueInCollection("sessions", 3, "description", ""); 
		assertValueInCollection("sessions", 3, "kms", "");
		assertValueInCollection("sessions", 3, "date", "");
		
		setValueInCollection("sessions", 1, "description", "");
		setValueInCollection("sessions", 1, "kms", "");
		setValueInCollection("sessions", 1, "date", "");
		setValueInCollection("sessions", 2, "kms", "3");
		execute("CRUD.save");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("description", "JUNIT TRAINING");
		assertCollectionRowCount("sessions", 2);
		assertValueInCollection("sessions", 0, "description", "RUNNING IN THE STREET"); 
		assertValueInCollection("sessions", 0, 1, "5");
		assertValueInCollection("sessions", 0, "date", currentDate);  
		assertValueInCollection("sessions", 1, "description", "WALKING"); 
		assertValueInCollection("sessions", 1, "kms", "3");
		assertValueInCollection("sessions", 1, "date", "3/1/14");		
		assertValueInCollection("sessions", 2, "description", ""); 
		assertValueInCollection("sessions", 2, "kms", "");
		assertValueInCollection("sessions", 2, "date", "");
		
		setValueInCollection("sessions", 2, "description", ""); 
		setValueInCollection("sessions", 2, "kms", "2");
		execute("CRUD.save");
		assertError("Value for Description in Training session is required");

		setValueInCollection("sessions", 2, "description", "THE LAST SESSION"); 
		setValueInCollection("sessions", 2, "kms", "1");
		execute("CRUD.save");
		assertError("1 is not a valid value for Kms of Training session: tiene que ser mayor o igual que 2");  

		setValueInCollection("sessions", 2, "kms", "51");
		execute("CRUD.save");
		assertError("51 is not a valid value for Kms of Training session: tiene que ser menor o igual que 50"); 		
		
		setValueInCollection("sessions", 2, "kms", "3");
		setValueInCollection("sessions", 3, "description", "THE LAST SESSION"); 
		setValueInCollection("sessions", 3, "kms", "4");
		execute("CRUD.save");		 
		assertError("More than 3 items in Sessions of Training are not allowed");
		assertErrorsCount(1);
		
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	public void testRemoveRowInElementCollection() throws Exception { 
		getWebClient().getOptions().setCssEnabled(true); 
		setValue("description", "JUNIT TRAINING");
		HtmlElement row = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Training__sessions___0"); 
		HtmlElement removeLink = row.getElementsByTagName("a").get(0);
		assertTrue(!removeLink.isDisplayed());
		setValueInCollection("sessions", 0, "description", "ONE");
		assertTrue(removeLink.isDisplayed()); 
		setValueInCollection("sessions", 0, "kms", "11");
		row = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Training__sessions___1"); 
		removeLink = row.getElementsByTagName("a").get(0);
		assertTrue(!removeLink.isDisplayed());		
		setValueInCollection("sessions", 1, "description", "TWO");
		assertTrue(removeLink.isDisplayed());
		setValueInCollection("sessions", 1, "kms", "12");		
		row = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Training__sessions___2"); 
		removeLink = row.getElementsByTagName("a").get(0);
		assertTrue(!removeLink.isDisplayed());
		setValueInCollection("sessions", 2, "description", "THREE"); 
		setValueInCollection("sessions", 2, "kms", "13");		
		execute("CRUD.save");
		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("sessions", 3);
		assertValueInCollection("sessions", 0, "description", "ONE"); 
		assertValueInCollection("sessions", 0, "kms", "11");
		assertValueInCollection("sessions", 1, "description", "TWO"); 
		assertValueInCollection("sessions", 1, "kms", "12");
		assertValueInCollection("sessions", 2, "description", "THREE"); 
		assertValueInCollection("sessions", 2, "kms", "13");		
		
		removeSessionsRow(1); 
		assertCollectionRowCount("sessions", 2);
		assertValueInCollection("sessions", 0, "description", "ONE"); 
		assertValueInCollection("sessions", 0, "kms", "11");
		assertValueInCollection("sessions", 1, "description", "THREE"); 
		assertValueInCollection("sessions", 1, "kms", "13");				
		
		execute("CRUD.save");		
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("sessions", 2);
		assertValueInCollection("sessions", 0, "description", "ONE"); 
		assertValueInCollection("sessions", 0, "kms", "11");
		assertValueInCollection("sessions", 1, "description", "THREE"); 
		assertValueInCollection("sessions", 1, "kms", "13");

		setValueInCollection("sessions", 2, "description", "FOUR");
		setValueInCollection("sessions", 2, "kms", "14");
		setValueInCollection("sessions", 3, "description", "FIVE");
		setValueInCollection("sessions", 3, "kms", "15");

		assertCollectionRowCount("sessions", 4);
		assertValueInCollection("sessions", 0, "description", "ONE"); 
		assertValueInCollection("sessions", 0, "kms", "11");
		assertValueInCollection("sessions", 1, "description", "THREE"); 
		assertValueInCollection("sessions", 1, "kms", "13");			
		assertValueInCollection("sessions", 2, "description", "FOUR"); 
		assertValueInCollection("sessions", 2, "kms", "14");
		assertValueInCollection("sessions", 3, "description", "FIVE"); 
		assertValueInCollection("sessions", 3, "kms", "15");
		
		removeSessionsRow(2); 
		
		assertCollectionRowCount("sessions", 3);
		assertValueInCollection("sessions", 0, "description", "ONE"); 
		assertValueInCollection("sessions", 0, "kms", "11");
		assertValueInCollection("sessions", 1, "description", "THREE"); 
		assertValueInCollection("sessions", 1, "kms", "13");							
		assertValueInCollection("sessions", 2, "description", "FIVE"); 
		assertValueInCollection("sessions", 2, "kms", "15");
		
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("sessions", 3);
		assertValueInCollection("sessions", 0, "description", "ONE"); 
		assertValueInCollection("sessions", 0, "kms", "11");
		assertValueInCollection("sessions", 1, "description", "THREE"); 
		assertValueInCollection("sessions", 1, "kms", "13");							
		assertValueInCollection("sessions", 2, "description", "FIVE"); 
		assertValueInCollection("sessions", 2, "kms", "15");
		
		removeSessionsRow(0);
		
		assertCollectionRowCount("sessions", 2);
		assertValueInCollection("sessions", 0, "description", "THREE"); 
		assertValueInCollection("sessions", 0, "kms", "13");							
		assertValueInCollection("sessions", 1, "description", "FIVE"); 
		assertValueInCollection("sessions", 1, "kms", "15");

		removeSessionsRow(0);
		
		assertCollectionRowCount("sessions", 1);
		assertValueInCollection("sessions", 0, "description", "FIVE"); 
		assertValueInCollection("sessions", 0, "kms", "15");
		
		execute("CRUD.save");
		assertNoErrors(); 
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("sessions", 1); 
		assertValueInCollection("sessions", 0, "description", "FIVE"); 
		assertValueInCollection("sessions", 0, "kms", "15");		
						
		execute("CRUD.delete");
		assertNoErrors();
		
		execute("CRUD.new");		
		setValue("description", "JUNIT TRAINING");
		setValueInCollection("sessions", 0, "description", "ONE");
		setValueInCollection("sessions", 0, "kms", "11");
		setValueInCollection("sessions", 1, "description", "TWO");
		setValueInCollection("sessions", 1, "kms", "12");		
		setValueInCollection("sessions", 2, "description", "THREE"); 
		setValueInCollection("sessions", 2, "kms", "13");
		removeSessionsRow(2);
		execute("CRUD.save");

		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("sessions", 2); 
						
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	private void removeSessionsRow(int rowIndex) throws Exception { 
		HtmlElement row = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Training__sessions___" + rowIndex); 
		HtmlElement removeIcon = row.getElementsByTagName("a").get(0).getElementsByTagName("i").get(0); 
		removeIcon.click();		
	}

	private void assertDateEditor() throws Exception {
		String html = getHtml();
		assertTrue(html.contains("javascript:showCalendar('ox_OpenXavaTest_Training__sessions___0___date'"));
		assertTrue(html.contains("<i class=\"mdi mdi-calendar\"")); 		
	}
		
}
