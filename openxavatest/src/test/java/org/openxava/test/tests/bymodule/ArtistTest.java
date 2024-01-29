package org.openxava.test.tests.bymodule;

import org.htmlunit.html.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class ArtistTest extends ModuleTestBase {
	
	public ArtistTest(String testName) {
		super(testName, "Artist");		
	}
	
	public void testBeanValidationJSR303_focusOnList_dialogFromOnChangeAction_noSpacesInDescriptionsList_editorForAnnotation_displaySizeGreaterThan50WhenColumnLength255() throws Exception {   
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
		assertError("99 is not a valid value for Age of Artist: must be less than or equal to 90"); 

		assertErrorStyle(); 
		
		// @DisplaySize greater than 50 when @Column(length=255) 
		HtmlElement nameField = getHtmlPage().getHtmlElementById("ox_openxavatest_Artist__name");
		assertEquals("255", nameField.getAttribute("maxlength"));
		assertEquals("70", nameField.getAttribute("size"));
		
		// Dialog from OnChange action
		assertExists("age");
		setValue("name", "CHARLOT");				
		assertDialog();
		assertDialogTitle("Are you sure to change the name?");
		assertValue("name", "CHARLOT");
		assertNotExists("age");		
	}
	
	private void assertEditorForAnnotation(String property, String color) { 
		String editorHTML = getHtmlPage().getHtmlElementById("ox_openxavatest_Artist__editor_" + property).asXml();
		assertTrue(editorHTML.contains("class=\"colorful-color-" + color + " colorful-background-yellow\"")); 		 
	}

	private void assertErrorStyle() throws Exception {
		HtmlSpan age = (HtmlSpan) getHtmlPage().getElementById("ox_openxavatest_Artist__editor_age");
		assertTrue("age has no error style", age.getAttribute("class").contains("ox-error-editor")); 

	}
		
}
