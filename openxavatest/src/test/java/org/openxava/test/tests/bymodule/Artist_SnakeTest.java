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
		// tmr ¿Etiqueta automáticas?
		assertValueInList(0, 0, ""); // tmr ¿Propiedades automáticas en modo lista como bug aparte?
		assertValueInList(0, 1, "AARON");
		assertValueInList(0, 2, "");
		assertValueInList(0, 3, "B");
		assertValueInList(0, 4, "MAIN CHARACTER");
		
		execute("List.viewDetail", "row=0");
		assertExists("artist_studio.name");
		assertValue("artist_name", "AARON");
		assertExists("artist_age");
		assertDescriptionValue("artist_level.id", "B MAIN CHARACTER");
		
		assertLabel("artist_name", "Artist name"); // tmr ¿Otro bug en changelog?
	}
			
}
