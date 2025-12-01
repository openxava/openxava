package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class OrderDocumentTest extends ModuleTestBase {
		
	public OrderDocumentTest(String testName) {
		super(testName, "OrderDocument");		
	}
			
	public void testGroupByIncludesPropertiesFromReferences_CalculationPropertyInReferenceInsideHiddenSection() throws Exception {
		getWebClient().getOptions().setCssEnabled(true);
		
		selectGroupBy("Group by year");
		assertNoErrors();
		assertListRowCount(1); 
		assertValueInList(0, 0, "2024");
		assertValueInList(0, 1, "26.62");
		assertLabelInList(1, "Invoice total amount"); // No to test label, but to be sure that is the property from the reference
		selectGroupBy("No grouping");
		
		execute("List.viewDetail", "row=0");
		assertValue("year", "2024");
		assertFalse(getHtmlPage().getElementById("xava_loading").isDisplayed());		
	}
				
}
