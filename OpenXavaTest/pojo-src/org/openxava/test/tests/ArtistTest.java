package org.openxava.test.tests;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

import com.gargoylesoftware.htmlunit.html.*;

/**
 * @author Javier Paniza
 */

public class ArtistTest extends ModuleTestBase {
	
	public ArtistTest(String testName) {
		super(testName, "Artist");		
	}
	
	public void testBeanValidationJSR303_focusOnList_dialogFromOnChangeAction_noSpacesInDescriptionsList_editorForAnnotation() throws Exception {  
		// Focus on list
		assertFocusOn("listConfigurations");
		execute("List.filter"); 
		assertFocusOn("listConfigurations"); 
		
		// No spaces in descriptions list
		execute("List.viewDetail", "row=0");
		ActingLevel level = XPersistence.getManager().find(ActingLevel.class, "B    ");
		assertEquals(5, level.getId().length());
		assertEquals(40, level.getDescription().length());
		assertDescriptionValue("level.id", "B MAIN CHARACTER"); 
		
		// Editor for annotation
		assertEditorForAnnotation("name", "green");
		assertEditorForAnnotation("age", "pink");
		
		// Bean Validation JSR 303
		setValue("age", "99");		
		execute("CRUD.save");		
		// tmr assertError("99 is not a valid value for Age of Artist: tiene que ser menor o igual que 90"); 
		assertError("99 is not a valid value for Age of Artist: must be less than or equal to 90"); // tmr

		assertErrorStyle(); 
		
		// Dialog from OnChange action
		assertExists("age");
		setValue("name", "CHARLOT");				
		assertDialog();
		assertDialogTitle("Are you sure to change the name?");
		assertValue("name", "CHARLOT");
		assertNotExists("age");		
	}
	
	private void assertEditorForAnnotation(String property, String color) { 
		String editorHTML = getHtmlPage().getHtmlElementById("ox_OpenXavaTest_Artist__editor_" + property).asXml();
		assertTrue(editorHTML.contains("color: " + color + ";"));
		assertTrue(editorHTML.contains("background: yellow;"));
		assertTrue(editorHTML.contains("class=\"ox-colorful-" + color + "\""));
	}

	private void assertErrorStyle() throws Exception {
		HtmlSpan age = (HtmlSpan) getHtmlPage().getElementById("ox_OpenXavaTest_Artist__editor_age");
		assertTrue("age has no error style", age.getAttribute("class").contains("ox-error-editor")); 

	}
		
}
