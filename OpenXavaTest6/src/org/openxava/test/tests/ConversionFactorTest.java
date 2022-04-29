package org.openxava.test.tests;

import org.openxava.jpa.XPersistence;
import org.openxava.tests.ModuleTestBase;

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
	
	public void testDigits() throws Exception { 
		execute("CRUD.new");
		setValue("id", "1");
		setValue("fromUnit", "GALLONS");
		setValue("toUnit", "CUBIC FEET");
		setValue("factor", "0.133681");
		setValue("reverseFactor", "7.480519");
		execute("CRUD.save");
		execute("CRUD.new");
		setValue("id", "1");
		execute("CRUD.refresh");
		assertValue("fromUnit", "GALLONS"); 
		assertValue("toUnit", "CUBIC FEET");
		assertValue("factor", "0.133681");
		assertValue("reverseFactor", "7.480519");
	}
}
