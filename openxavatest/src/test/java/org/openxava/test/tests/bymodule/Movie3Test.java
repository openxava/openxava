package org.openxava.test.tests.bymodule;

import java.util.*;

/** 
 * 
 * @author Javier Paniza
 */
public class Movie3Test extends MovieBaseTest { 
		
	public Movie3Test(String testName) {
		super(testName, "Movie3"); 
	}
	
	public void testShowFileNameForFileAnnotationWhenPrint() throws Exception {
		execute("Print.generatePdf");
		assertContentTypeForPopup("application/pdf"); 
		assertPopupPDFLine(4, "FORREST GUMP ROBERT ZEMECKIS ERIC ROTH Y WINSTON GROOM 7/6/1994 Forrest Gump Trailer.webm");
		assertPopupPDFLine(5, "GATTACA gattaca-trailer.png");

		execute("Print.generateExcel");
		assertContentTypeForPopup("text/x-csv");		
		StringTokenizer excel = new StringTokenizer(getPopupText(), "\n\r");
		excel.nextToken();
		String line1 = excel.nextToken();
		assertEquals("\"FORREST GUMP\";\"ROBERT ZEMECKIS\";\"ERIC ROTH Y WINSTON GROOM\";\"7/6/1994\";\"Forrest Gump Trailer.webm\"", line1);
		String line2 = excel.nextToken();
		assertEquals("\"GATTACA\";\"\";\"\";;\"gattaca-trailer.png\"", line2);
	}
		
}