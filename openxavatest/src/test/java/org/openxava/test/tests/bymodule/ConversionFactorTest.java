package org.openxava.test.tests.bymodule;

import org.htmlunit.html.*;
import org.openxava.jpa.*;
import org.openxava.tests.*;

public class ConversionFactorTest extends ModuleTestBase {

	public ConversionFactorTest(String testName) {
		super(testName, "ConversionFactor");
	}
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		XPersistence.getManager().createQuery("delete from ConversionFactor")
				.executeUpdate();
		XPersistence.commit(); 
	}
	
	public void testDigits_columnScale() throws Exception {
		execute("CRUD.new");
		// tmr ini
		// TMR ME QUEDÉ POR AQUÍ:
		// TMR   THIS TEST WORKS WELL AND IT SEEMS THE BUG IS FIXED, BUT WITH EXAMPLES OF THE ISSUE IT DOES NOT WORK
		// TMR   IT FAILS ON LEAVE THE FOCUS. MOREOVER, IT SEEMS THAT WORKS THE SAME WITH AND WITHOUT THE FIX
		HtmlInput reverseFactorField = getHtmlPage().getHtmlElementById("ox_openxavatest_ConversionFactor__reverseFactor");
		reverseFactorField.type("-91,234,567,890.123456");
		assertEquals("-91,234,567,890.12345", reverseFactorField.getValue());
		// tmr fin
		/* tmr
		setValue("id", "1");
		setValue("fromUnit", "GALLONS");
		setValue("toUnit", "CUBIC FEET");
		setValue("factor", "0.133681");
		setValue("reverseFactor", "7.480519");
		execute("CRUD.save");
		assertNoErrors(); // tmr
		execute("CRUD.new");
		setValue("id", "1");
		execute("CRUD.refresh");
		assertValue("fromUnit", "GALLONS"); 
		assertValue("toUnit", "CUBIC FEET");
		assertValue("factor", "0.133681");
		System.out.println("[ConversionFactorTest.testDigits_columnScale] reverseFactor=" + getValue("reverseFactor")); // tmr
		assertValue("reverseFactor", "7.480519");
		assertValue("shortFactor", "0.13");
		assertValue("factorIndex", "133,681");
		assertValue("factorGrade", "133,681");
		*/
	}
}
