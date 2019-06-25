package org.openxava.test.tests;

import java.time.*;
import java.time.format.*;
import org.openxava.tests.*;

/**
 *
 * @author Javier Paniza
 */

public class Invoice6Test extends ModuleTestBase {
	
	private String hoy= LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
	
	public Invoice6Test(String testName) {
		super(testName, "Invoice6");		
	}
	
	public void testLocalDate() throws Exception {
		assertValueInList(0, 2, "1/1/02");
		assertValueInList(1, 2, "1/4/04");
		execute("List.viewDetail", "row=0");
		assertValue("date", "1/1/02");
		setValue("date", "3/20/19");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertValueInList(0, 2, "3/20/19");
		
		setLocale("es");
		assertValueInList(0, 2, "20/03/2019");	
		execute("List.viewDetail", "row=0");
		assertValue("date", "20/03/2019");
		setValue("date", "27/05/2019");
		execute("CRUD.save"); 
		assertNoErrors();
		execute("Mode.list");
		assertValueInList(0, 2, "27/05/2019");

		execute("List.viewDetail", "row=0");
		
		assertParseLocalDate("21/03/19", "21/03/2019");
		assertParseLocalDate("220319", "22/03/2019");
		assertParseLocalDate("23032019", "23/03/2019");
		assertParseLocalDate("24.3.19", "24/03/2019");
		assertParseLocalDate("25.03.2019", "25/03/2019");
		assertParseLocalDate("26-3-19", "26/03/2019");
		assertParseLocalDate("27-03-2019", "27/03/2019");
		
		setValue("date", "32/03/2019");
		execute("CRUD.save");
		assertError("Fecha en Invoice 6 no es un dato del tipo esperado");
		
		setValue("date", "01/01/2002");
		execute("CRUD.save");
		assertNoErrors();	
		
		execute("Mode.list");
		assertListRowCount(9);
		setConditionValues("", "", "4/1/2004"); // With this format to verify that uses the formatter from editor
		execute("List.filter");
		assertListRowCount(2);
		assertValueInList(0, 2, "04/01/2004");
		assertValueInList(1, 2, "04/01/2004");		
	}
	
	public void testFilterByLocalDate() throws Exception {   
		String [] yearComparators = { "=", "=", "year_comparator"};
		setConditionComparators(yearComparators);
		
		String [] condition2004 = { " ", " ", "2004"}; 
		setConditionValues(condition2004);
		execute("List.filter");
		assertListRowCount(3);
		assertValueInList(0, 0, "2004");
		assertValueInList(0, 1, "2");
		assertValueInList(0, 2, "1/4/04");
		assertValueInList(1, 0, "2004");
		assertValueInList(1, 1, "9");
		assertValueInList(1, 2, "1/4/04");
		assertValueInList(2, 0, "2004");
		assertValueInList(2, 1, "10");
		assertValueInList(2, 2, "12/4/04");		
		

		String [] monthComparators = { "=", "=", "month_comparator"};
		setConditionComparators(monthComparators);		
		String [] conditionMonth1 = { " ", " ", "1" }; 
		setConditionValues(conditionMonth1);
		execute("List.filter");
		assertListRowCount(3);
		assertValueInList(0, 0, "2002");
		assertValueInList(0, 1, "1");
		assertValueInList(0, 2, "1/1/02");				
		assertValueInList(1, 0, "2004");
		assertValueInList(1, 1, "2");
		assertValueInList(1, 2, "1/4/04");
		assertValueInList(2, 0, "2004");
		assertValueInList(2, 1, "9");
		assertValueInList(2, 2, "1/4/04");
		
		String [] yearMonthComparators = { "=", "=", "year_month_comparator"};
		setConditionComparators(yearMonthComparators);		
		String [] conditionYear2004Month1 = { " ", " ", "2004/1" }; 
		setConditionValues(conditionYear2004Month1);
		execute("List.filter");
		assertListRowCount(2); 				
		assertValueInList(0, 0, "2004");
		assertValueInList(0, 1, "2");
		assertValueInList(0, 2, "1/4/04");
		assertValueInList(1, 0, "2004");
		assertValueInList(1, 1, "9");
		assertValueInList(1, 2, "1/4/04");		
	}
	
	public void testGroupByLocalDate() throws Exception {  

		assertAllGroupBys( 
			"No grouping",
			"Group by year",
			"Group by number",
			"Group by date",
			"Group by month of date",
			"Group by year of date",
			"Group by amounts sum"
		);
		
		assertListRowCount(9);
		assertValuesInList(0, "2002",  "1",  "1/1/02",  "2,500.00");
		assertValuesInList(1, "2004",  "2",  "1/4/04",     "11.00");
		assertValuesInList(2, "2004",  "9",  "1/4/04",  "4,396.00");
		assertValuesInList(3, "2004", "10", "12/4/04",  "1,189.00");
		assertValuesInList(4, "2004", "11", "11/4/06",      "0.00");
		assertValuesInList(5, "2004", "12", "11/5/06",    "110.00");
		assertValuesInList(6, "2007", "14", "5/28/07",  "6,059.00");
		assertValuesInList(7, "2009",  "1", "6/23/09",      "0.00");
		assertValuesInList(8, "2011",  "1", "3/14/11", "18,207.00");
		
		selectGroupBy("Group by date");
		assertListRowCount(8); 
		assertListColumnCount(3);
		assertValuesInList(0,  "1/1/02",  "2,500.00", "1");
		assertValuesInList(1,  "1/4/04",  "4,407.00", "2");
		assertValuesInList(2, "12/4/04",  "1,189.00", "1");
		assertValuesInList(3, "11/4/06",      "0.00", "1");
		assertValuesInList(4, "11/5/06",    "110.00", "1");
		assertValuesInList(5, "5/28/07",  "6,059.00", "1");
		assertValuesInList(6, "6/23/09",      "0.00", "1");
		assertValuesInList(7, "3/14/11", "18,207.00", "1");
		
		selectGroupBy("Group by month of date");
		assertListRowCount(7);
		assertListColumnCount(3);
		assertValuesInList(0, "2002/1",  "2,500.00", "1");
		assertValuesInList(1, "2004/1",  "4,407.00", "2");
		assertValuesInList(2, "2004/12", "1,189.00", "1");
		assertValuesInList(3, "2006/11",   "110.00", "2");
		assertValuesInList(4, "2007/5",  "6,059.00", "1");
		assertValuesInList(5, "2009/6",      "0.00", "1"); 
		assertValuesInList(6, "2011/3", "18,207.00", "1");
		
		selectGroupBy("Group by year of date");
		assertListRowCount(6);
		assertListColumnCount(3);
		assertValuesInList(0, "2002",  "2,500.00", "1");
		assertValuesInList(1, "2004",  "5,596.00", "3");
		assertValuesInList(2, "2006",    "110.00", "2");
		assertValuesInList(3, "2007",  "6,059.00", "1");
		assertValuesInList(4, "2009",      "0.00", "1");
		assertValuesInList(5, "2011", "18,207.00", "1");				
	}
	
	private void assertParseLocalDate(String originalDate, String parsedDate) throws Exception {
		setValue("date", originalDate);
		execute("CRUD.save"); 
		assertNoErrors();
		assertValue("date", hoy); // So we test default value calculator
		execute("Navigation.first");
		assertValue("date", parsedDate);
	}
			
}
