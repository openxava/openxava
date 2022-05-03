package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Jeromy Atuna
 */
public class PlayerTest extends ModuleTestBase {
	
	public PlayerTest(String testName) {
		super(testName, "Player");
	}
	
	public void testEntityValidatorMessageFromBeanValidation() throws Exception {
		execute("CRUD.new");
		setValue("name", "HUGO SOTIL");
		setValue("birthdate", "05/18/1949");
		execute("Player.save");
		assertError("HUGO SOTIL is not less than 40 years old"); 
	}
	
}
