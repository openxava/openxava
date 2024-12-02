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

		// @Digits(integer=10, fraction=6) does not allow more than 17 (16 + negative sign)
		HtmlInput reverseFactorField = getHtmlPage().getHtmlElementById("ox_openxavatest_ConversionFactor__reverseFactor");
		reverseFactorField.type("-91,234,567,890.123456");
		assertEquals("-91,234,567,890.12345", reverseFactorField.getValue());
		
		setValue("id", "1");
		setValue("fromUnit", "GALLONS");
		setValue("toUnit", "CUBIC FEET");
		setValue("factor", "0.133681");
		setValue("reverseFactor", "7.480519");
		setValue("storedFactorIndex", "133"); // tmr
		execute("CRUD.save");
		assertNoErrors(); 
		execute("CRUD.new");
		setValue("id", "1");
		execute("CRUD.refresh");
		assertValue("fromUnit", "GALLONS"); 
		assertValue("toUnit", "CUBIC FEET");
		assertValue("factor", "0.133681");
		assertValue("reverseFactor", "7.480519");
		assertValue("mirrorFactor", "0.133681"); 
		assertValue("shortFactor", "0.13");
		assertValue("factorIndex", "133,681");
		assertValue("factorGrade", "133,681");
		assertValue("storedFactorIndex", "133"); // tmr
	}
}
