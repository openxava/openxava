package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class FamilyXProductsReportTest extends ModuleTestBase {
	
		
	public FamilyXProductsReportTest(String nombreTest) {
		super(nombreTest, "Family1ProductsReport");		
	}
	
	public void testDescriptionsComboCacheNotSharedAndOrderInDescriptionList() throws Exception {
		String [][] subfamiliesFamily1 = {
			{ "", ""},
			{ "3", "SISTEMA"},			
			{ "2", "GESTION"},			
			{ "1", "DESARROLLO"}
		};		
		assertValidValues("subfamily.number", subfamiliesFamily1);
		assertAction("Reference.modify"); // To test that modify action is shown in descriptions list
		changeModule("Family2ProductsReport");
		String [][] subfamiliesFamily2 = {
			{ "", ""},
			{ "12", "PC"},
			{ "13", "PERIFERICOS"},			
			{ "11", "SERVIDORES"}						
		};		
		assertValidValues("subfamily.number", subfamiliesFamily2); 
		assertNoAction("Reference.modify"); // To test modify="false" in reference as descriptions list
	}
	
	public void testJasperReportBaseActionTest() throws Exception { 
		execute("FamilyProductsReport.generatePdf");
		// Next line: test that errors of a ValidationException thrown from a action are shown
		assertError("Value for Subfamily in Filter by subfamily is required"); 
		assertNoPopup();
		setValue("subfamily.number", "1");
		
		execute("FamilyProductsReport.generatePdf"); // takes-long is tested too (only testing that no crash)
		assertNoErrors(); // To test that the main page is refreshed
		assertContentTypeForPopup("application/pdf");
		
		execute("FamilyProductsReport.generateExcel"); 		
		assertContentTypeForPopup("application/vnd.ms-excel"); 		 
	}

}
