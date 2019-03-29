package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class SalesRepresentativePersonOnlyNamesTest extends ModuleTestBase {
	
	public SalesRepresentativePersonOnlyNamesTest(String testName) {
		super(testName, "SalesRepresentativePersonOnlyNames");		
	}
	
	public void testEditorForViewsInReference() throws Exception { 
		execute("CRUD.new");
		setValue("person.personFirstName", "javi");		
		execute("CRUD.save");
		assertValue("person.personFirstName", "Javi"); // Because of PersonName editor
	}
			
}
