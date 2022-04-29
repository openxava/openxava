package org.openxava.test.tests;

import org.openxava.test.model.*;
import org.openxava.jpa.*;
import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class PhoneTest extends ModuleTestBase {
	
	public PhoneTest(String testName) {
		super(testName, "Phone");		
	}
	
	public void testEntityValidatorInjectingAPropertyWithTheSameNameOfTheEntity() throws Exception {
		Country country = XPersistence.getManager().find(Country.class, "ff8080822d278d29012d27909d220002");
		assertEquals("ALEMANIA", country.getName());  
		execute("CRUD.new");
		setValue("phoneCountry.id", country.getId());  		
		setValue("phone", "147 00 98");
		setValue("phoneExtension", "96");
		execute("CRUD.save");
		assertNoErrors();
		XPersistence.getManager().refresh(country);
		assertEquals("ALEMANIAX", country.getName());
		country.setName("ALEMANIA");
	}
	
}
