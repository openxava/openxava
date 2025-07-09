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
		// TMR ME QUEDÉ POR AQUÍ: YA FUNCIONA PARA PROPIEDADES SIMPLE, FALTA PROBAR LOS @DescriptionsList
		// tmr Comprobar que las trazas en el servidor no son culpa de la nueva funcionalidad
		assertValueInList(0, "unitPrice", "11.00"); 
		assertValueInList(1, "unitPrice", "23.00");
		assertValueInList(2, "unitPrice",  "0.00");
		assertEditableInList(0, "unitPrice");
		assertEditableInList(1, "unitPrice");
		assertEditableInList(2, "unitPrice");
		assertNoEditableInList(0, "extendedDescription"); // Included in editableProperties but it's @Formula so not should be editable
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
