package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * tmr
 * @author Javier Paniza
 */

public class Artist_SnakeTest extends ModuleTestBase {
	
	public Artist_SnakeTest(String testName) {
		super(testName, "Artist_Snake");		
	}
	
	public void testSnakeCase() throws Exception { 
		// tmr Falta testear que la lista tiene todas las columnas que Artist con los datos correctos, podría ser un bug aparte en changelog
		
		// TMR ME QUEDÉ POR AQUÍ: TEST RECIEN HECHO FALLA
		execute("List.viewDetail", "row=0");
		assertExists("artist_studio.name");
		assertValue("artist_name", "AARON");
		assertExists("artist_age");
		assertDescriptionValue("artist_level.id", "B MAIN CHARACTER");
	}
			
}
