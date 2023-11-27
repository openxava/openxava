package org.openxava.test.bymodule;

import org.openxava.tests.*;


/**
 * @author Javier Paniza
 */

public class Color2Test extends ModuleTestBase {
	
	public Color2Test(String testName) {
		super(testName, "Color2");		
	}
	
	public void testDescriptionsListInMyReport() throws Exception { 
		execute("ExtendedPrint.myReports");
		assertValueInCollection("columns", 4, 0, "Used to"); 
		execute("MyReport.editColumn", "row=4,viewObject=xava_view_columns");
		assertNotExists("comparator");
		assertNotExists("value");
		assertNotExists("dateValue");
		assertNotExists("booleanValue");
		assertNotExists("validValuesValue"); 
		assertExists("descriptionsListValue"); 
		assertExists("order");
		
		setValue("name", "number");
		assertExists("comparator"); 
		assertExists("value");
		assertNotExists("dateValue");
		assertNotExists("booleanValue");
		assertNotExists("validValuesValue"); 
		assertNotExists("descriptionsListValue"); 
		assertExists("order");
		
		setValue("name", "usedTo.name");
		assertNotExists("comparator");		
		assertNotExists("value");
		assertNotExists("dateValue");
		assertNotExists("booleanValue");
		assertNotExists("validValuesValue"); 
		assertExists("descriptionsListValue"); 
		assertExists("order");

		String [][] validValues = {
			{ "", "" },
			{ "LAMPPOST", "LAMPPOST" },
			{ "HOUSE", "HOUSE" },
			{ "DOOR", "DOOR" },
			{ "CAR", "CAR" } 
		};		
		
		assertValidValues("descriptionsListValue", validValues); 
		assertValue("descriptionsListValue", "");
		setValue("descriptionsListValue", "CAR"); 
		execute("MyReport.saveColumn");
		assertValueInCollection("columns", 4, 2, "CAR"); 
		
		execute("MyReport.editColumn", "row=4,viewObject=xava_view_columns"); 		
		assertValue("descriptionsListValue", "CAR"); 
		closeDialog();
		
		execute("MyReport.generatePdf"); 
		assertPopupPDFLinesCount(5);  
		assertPopupPDFLine(3, "0 ROJO FF0000 RED CAR 3 PLACES CAR");
		
		execute("ExtendedPrint.myReports");
		assertValueInCollection("columns", 4, 0, "Used to"); 
		assertValueInCollection("columns", 4, 2, "CAR");
		execute("MyReport.editColumn", "row=4,viewObject=xava_view_columns");
		assertValue("descriptionsListValue", "CAR"); 
		closeDialog();
		
		execute("MyReport.remove", "xava.keyProperty=name");				
	}
	
	public void testFilterDescriptionsList_keyReferenceWithSameNameThatPropertyFather_twoReferencesToSameModelInList() throws Exception {  
		assertLabelInList(6, "Characteristic thing thing"); 
		assertLabelInList(7, "Another ct thing");
		assertValueInList(3, 6, "CAR");
		assertValueInList(3, 7, "LAMPPOST");
		assertLabelInList(4, "Used to"); 
		String [][] validValues = {
			{ "", "" },
			{ "LAMPPOST", "LAMPPOST" },
			{ "HOUSE", "HOUSE" },
			{ "DOOR", "DOOR" },
			{ "CAR", "CAR" }
		};		
		assertValidValues("conditionValue___3", validValues); 		
		assertValueInList(0, 4, "CAR");
		setConditionValues(new String[] { "", "", "", "CAR"} ); 
		// execute("List.filter"); // Not needed because filterOnChange=true
		assertListRowCount(1); 
	}
		
}