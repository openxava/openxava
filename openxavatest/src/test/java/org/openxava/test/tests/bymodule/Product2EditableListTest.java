package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class Product2EditableListTest extends ModuleTestBase {
	
	public Product2EditableListTest(String testName) {
		super(testName, "Product2EditableList");		
	}
	
	public void testEditablePropertiesInList() throws Exception {
		// TMR ME QUEDÉ POR AQUÍ: COMPROBÉ QUE LA NUEVA IMPLEMENTACIÓN DE assertEditableInList
		// TMR   Y assertNoEditableInList FUNCIONAN CON PROPIEDADES SIMPLE
		// TMR   FALTA ADAPTARLAS PARA LOS @DescriptionsList Y COMPLETAR LA PRUEBA
		
		// Simple property
		assertValueInList(0, "unitPrice", "11.00"); 
		assertValueInList(1, "unitPrice", "23.00");
		assertValueInList(2, "unitPrice",  "0.00");
		assertEditableInList(0, "unitPrice");
		assertEditableInList(1, "unitPrice");
		assertEditableInList(2, "unitPrice");

		
		// @DescriptionsList with single key
		assertValueInList(0, "family.description", "SOFTWARE");
		assertValueInList(1, "family.description", "HARDWARE");
		assertValueInList(2, "family.description", "SERVICIOS");
		assertEditableInList(0, "family.description");
		assertEditableInList(1, "family.description");
		assertEditableInList(2, "family.description");

		// @DescriptionsList with multiple keys
		assertValueInList(0, "subfamily.description", "Subfamily 2");
		assertValueInList(1, "subfamily.description", "Subfamily 2");
		assertValueInList(2, "subfamily.description", "Subfamily 2");
		assertEditableInList(0, "subfamily.description");
		assertEditableInList(1, "subfamily.description");
		assertEditableInList(2, "subfamily.description");

		// @DescriptionsList with multiple keys
		assertValueInList(0, "subfamily.description", "Subfamily 2");
		assertValueInList(1, "subfamily.description", "Subfamily 2");
		assertValueInList(2, "subfamily.description", "Subfamily 2");
		assertEditableInList(0, "subfamily.description");
		assertEditableInList(1, "subfamily.description");
		assertEditableInList(2, "subfamily.description");

		// Included in editableProperties but it's @Formula so not should be editable
		assertNoEditableInList(0, "extendedDescription"); 
		assertNoEditableInList(1, "extendedDescription");
		assertNoEditableInList(2, "extendedDescription");
		
		setValueInList(0, "unitPrice", "17");
		assertNoErrors();
		assertMessage("Saved new value for unit price in row 1");
		
		setValueInList(1, "unitPrice", "31");
		assertNoErrors();
		assertMessage("Saved new value for unit price in row 2");
		
		setValueInList(1, "unitPrice", "1500");
		assertError("{0} in {1} can not be greater than 1000"); // {0} and {1} should be replaced, but it fails too in detail mode, so it's another different bug
		
		execute("List.viewDetail", "row=0");
		assertValue("unitPrice", "17.00");
		
		execute("Mode.list");
		
		assertValueInList(0, "unitPrice", "17.00");
		assertValueInList(1, "unitPrice", "31.00");
		assertValueInList(2, "unitPrice",  "0.00");
		
		// Restoring
		setValueInList(0, "unitPrice", "11.00");
		setValueInList(1, "unitPrice", "23.00");
	}
	
}
