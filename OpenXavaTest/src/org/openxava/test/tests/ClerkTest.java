package org.openxava.test.tests;

import java.text.*;
import java.util.*;

import org.openxava.jpa.*;
import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;



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
	
	public void testTimeStereotypeAndSqlTimeAndStringAsByteArrayInDB_i18nOverXmlLabeInXmlComponents_generateRealExcel() throws Exception {
		assertListNotEmpty();
		execute("List.viewDetail", "row=0");
		if (!usesAnnotatedPOJO()) { // Only has sense in XML components
			assertLabel("arrivalTime", "The arrival time");  // Since v5.8 label of <property/> takes preference over i18n files
		}
		String time = getCurrentTime();
		setValue("arrivalTime", time);
		setValue("endingTime", time);
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
		
		// Asserting that java.sql.Time works in JasperReport
		execute("Print.generatePdf"); 		
		assertContentTypeForPopup("application/pdf");	
		
		// Real Excel
		execute("TypicalRealExcel.generateExcel");
		assertNoErrors(); 
		assertContentTypeForPopup("application/vnd.ms-excel");				
	}
	
	public void testListFormatSelectedButtonStyle() throws Exception { 
		HtmlAnchor listLink = getHtmlPage().getAnchorByHref("javascript:openxava.executeAction('OpenXavaTest', 'Clerk', '', false, 'ListFormat.select', 'editor=List')");
		assertTrue(listLink.getAttribute("class").contains("ox-selected-list-format"));
		HtmlAnchor chartsLink = getHtmlPage().getAnchorByHref("javascript:openxava.executeAction('OpenXavaTest', 'Clerk', '', false, 'ListFormat.select', 'editor=Charts')");
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
