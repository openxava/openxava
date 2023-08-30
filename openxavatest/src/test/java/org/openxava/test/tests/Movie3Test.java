package org.openxava.test.tests;

import java.util.*;

/** 
 * 
 * @author Javier Paniza
 */
public class Movie3Test extends MovieBaseTest { 
		
	public Movie3Test(String testName) {
		super(testName, "Movie3"); 
	}
	
	public void testShowImageForFileAnnotationWhenPrint() throws Exception {
		execute("Print.generatePdf");
		assertContentTypeForPopup("application/pdf");  
		System.out.println(getPopupPDFLine(4));
		//GATTACA 402881187bf356c6017bf36359cc0004
		//assertPopupPDFLinesCount(6);
		//assertPopupPDFLine(1, "Orphans of Orphanage: EL INTERNADO");
		
		execute("Print.generateExcel");
		assertContentTypeForPopup("text/x-csv");		
		StringTokenizer excel = new StringTokenizer(getPopupText(), "\n\r");
		excel.nextToken();
		excel.nextToken();
		String line1 = excel.nextToken();
		System.out.println(line1);
		//"GATTACA";"";"";;"402881187bf356c6017bf36359cc0004"
		//assertEquals("2017;1;\"5/10/2017\";1;\"Javi\";\"\";\"No\";\"110.00\"", line1);
	}
		
}