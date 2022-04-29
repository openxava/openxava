package org.openxava.test.tests;

import org.openxava.tests.*;



/**
 * @author Javier Paniza
 */

public class SummerCourseTest extends ModuleTestBase {
	
	public SummerCourseTest(String testName) {
		super(testName, "SummerCourse");		
	}
	
	public void testRemovingAChildClassWithCompositeKey() throws Exception {
		setValue("year", "2009");
		setValue("description", "CURSO DE VERANO");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertListRowCount(1);
		checkRow(0);
		execute("CRUD.deleteSelected");
		assertNoErrors();
		assertListRowCount(0);
	}
	
}
