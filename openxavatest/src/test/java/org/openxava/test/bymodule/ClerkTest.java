package org.openxava.test.bymodule;

import java.text.*;
import java.util.*;

import org.htmlunit.html.*;
import org.openxava.jpa.*;
import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class ClerkTest extends ModuleTestBase {
	
	public ClerkTest(String testName) {
		super(testName, "Clerk");		
	}
	
	public void testTextFieldsWithQuotationMarks_generateCustomExcel() throws Exception {
		assertListNotEmpty();
		execute("List.viewDetail", "row=0");
		String name = getValue("name");		
		String quotedName = name + "\"EL BUENO\"";
		setValue("name", quotedName);				
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("name", quotedName);
				
		// Restoring
		setValue("name", name);
		execute("CRUD.save");
		assertNoErrors();
		
		// Custom Excel
		execute("Clerk.createMyExcel");
		assertNoErrors(); 
		assertContentTypeForPopup("application/vnd.ms-excel");
	}
	
	public void testStringTimeAnnotationAndSqlTimeAndStringAsByteArrayInDB_i18nOverXmlLabeInXmlComponents_defaultSizeByAnnotation() throws Exception {  
		assertListNotEmpty();
		execute("List.viewDetail", "row=0");
		String time = getCurrentTime();
		setValue("arrivalTime", time);
		setValue("endingTime", time);
		assertSize("endingTime", "5"); 
		setValue("comments", "Created at " + time);
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertValueInList(0, "arrivalTime", time + ":00");
		assertValueInList(0, "endingTime", time);
		assertValueInList(0, "comments", "Created at " + time);
		
		setConditionValues(new String [] { "", "", "", "", time });
		execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, "arrivalTime", time + ":00");
		assertValueInList(0, "endingTime", time);
		
		setConditionValues(new String [] { "", "", "", "", "" });
		execute("List.filter");
		assertValueInList(1, "name", "JUAN");
		assertValueInList(1, "arrivalTime", "");
		assertValueInList(1, "endingTime", "");
		execute("List.viewDetail", "row=1");
		setValue("arrivalTime", "");
		setValue("endingTime", "");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertValueInList(1, "name", "JUAN");
		assertValueInList(1, "arrivalTime", "");
		assertValueInList(1, "endingTime", "");		
		
		// Asserting that java.sql.Time works in JasperReport
		execute("Print.generatePdf"); 		
		assertContentTypeForPopup("application/pdf");			
	}
	
	private void assertSize(String property, String expectedSize) {
		HtmlInput input = getHtmlPage().getHtmlElementById("ox_openxavatest_Clerk__" + property);
		assertEquals(expectedSize, input.getSize());
	}

	public void testListFormatSelectedButtonStyle() throws Exception { 
		HtmlElement listLink = getHtmlPage().getBody().getOneHtmlElementByAttribute("a", "data-argv", "editor=List"); 
		assertTrue(listLink.getAttribute("class").contains("ox-selected-list-format"));
		HtmlElement chartsLink = getHtmlPage().getBody().getOneHtmlElementByAttribute("a", "data-argv", "editor=Charts"); 
		assertFalse(chartsLink.getAttribute("class").contains("ox-selected-list-format"));
		
		HtmlElement iCharts = chartsLink.getElementsByTagName("i").get(0); 
		iCharts.click();		
		assertFalse(listLink.getAttribute("class").contains("ox-selected-list-format"));
		assertTrue(chartsLink.getAttribute("class").contains("ox-selected-list-format"));
	}
	
	public void testFilterInListBooleanWithNulls() throws Exception {  
		Collection withNulls = XPersistence.getManager().createQuery("from Clerk c where c.onVacation is null").getResultList();
		assertTrue(!withNulls.isEmpty());
		assertListRowCount(9);
		setConditionComparators("=", "=", "=", "=", "=", "=", "=", "=");
		setConditionValues("", "", "", "", "", "", "", "true");
		execute("List.filter");
		assertListRowCount(1);
		setConditionComparators("=", "=", "=", "=", "=", "=", "=", "<>");
		setConditionValues("", "", "", "", "", "", "", "true");
		execute("List.filter");
		assertListRowCount(8);
	}
	
	private String getCurrentTime() {
		return new SimpleDateFormat("HH:mm").format(new Date());
	}
	
}
