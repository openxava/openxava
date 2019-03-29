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

	private void assertParseLocalDate(String originalDate, String parsedDate) throws Exception {
		setValue("date", originalDate);
		execute("CRUD.save"); 
		assertNoErrors();
		assertValue("date", hoy); // So we test default value calculator
		execute("Navigation.first");
		assertValue("date", parsedDate);
	}
			
}
