package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class JavaProgrammerExtendedViewsTest extends ModuleTestBase {
	
	public JavaProgrammerExtendedViewsTest(String testName) {
		super(testName, "JavaProgrammerExtendedViews");		
	}
	
	public void testExtendsView() throws Exception { 
		execute("CRUD.new");
		// Starts with WithSectionsNoFavouriteFramework view
		assertExists("name");
		assertExists("sex"); 
		assertExists("mainLanguage");
		assertNotExists("favouriteFramework");
		assertExists("experiences");
		assertNotExists("frameworks");		
		execute("Sections.change", "activeSection=1");
		assertNotExists("experiences");
		assertExists("frameworks");
		execute("Sections.change", "activeSection=0");
		
		execute("JavaProgrammer.changeToDefaultView");
		assertExists("name");
		assertExists("sex"); 
		assertExists("mainLanguage");
		assertExists("favouriteFramework");
		assertExists("experiences");		
		assertNotExists("frameworks");
		assertNoAction("Sections.change");
		
		execute("JavaProgrammer.changeToWithSectionsView");
		assertExists("name");
		assertExists("sex"); 
		assertExists("mainLanguage");
		assertExists("favouriteFramework");
		assertExists("experiences");
		assertNotExists("frameworks");		
		execute("Sections.change", "activeSection=1");
		assertNotExists("experiences");
		assertExists("frameworks");
		
		execute("JavaProgrammer.changeToVerySimpleView");
		assertExists("name");
		assertExists("sex"); 
		assertNotExists("mainLanguage");
		assertNotExists("favouriteFramework");
		assertNotExists("experiences");
		assertNotExists("frameworks");
		assertNoAction("Sections.change");

		execute("JavaProgrammer.changeToSimpleView");
		assertExists("name"); 
		assertExists("sex"); 
		assertExists("mainLanguage");
		assertNotExists("favouriteFramework");
		assertNotExists("experiences");
		assertNotExists("frameworks");
		assertNoAction("Sections.change");
		
		execute("JavaProgrammer.changeToCompleteView");
		assertExists("name");
		assertExists("sex"); 
		assertExists("mainLanguage");
		assertExists("favouriteFramework");
		assertExists("experiences");
		assertExists("frameworks");
		assertNoAction("Sections.change");
		
		execute("JavaProgrammer.changeToWithSectionsAsProgrammerView");
		assertExists("name");
		assertExists("sex"); 
		assertExists("mainLanguage");
		assertNotExists("favouriteFramework");
		assertExists("experiences");
		assertNotExists("frameworks");
	}
			
}
