package org.openxava.test.tests;

import org.apache.commons.logging.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class ColorExtendedPrintTest extends ModuleTestBase {
	private static Log log = LogFactory.getLog(ColorExtendedPrintTest.class);
	
	public ColorExtendedPrintTest(String testName) {
		super(testName, "ColorExtendedPrint");		
	}
	
	public void testSharedReport() throws Exception{
		// we need that there is not any report
		execute("ExtendedPrint.myReports");
		assertDialogTitle("My reports");
		assertEditable("name");
		assertNoAction("MyReport.createNew");
		assertNoAction("MyReport.remove");
		assertNoAction("MyReport.share");
				
		// create a new report 
		setValue("name", "This is an report to share");
		checkRowCollection("columns", 2);
		checkRowCollection("columns", 3);
		checkRowCollection("columns", 4);
		checkRowCollection("columns", 5);
		execute("MyReport.removeColumn", "viewObject=xava_view_columns");
		assertCollectionRowCount("columns", 2);
		execute("MyReport.editColumn", "row=1,viewObject=xava_view_columns");
		setValue("value", "rojo");
		execute("MyReport.saveColumn");
		assertDialogTitle("My reports");
		execute("MyReport.generatePdf");
		assertNoDialog();
		assertNoErrors();
		
		// shared
		execute("ExtendedPrint.myReports");
		assertAction("MyReport.createNew");
		assertAction("MyReport.remove");
		assertAction("MyReport.share");
		assertValidValues("name", new String[][] { 
			{"This is an report to share",
			"This is an report to share"}
		});
		execute("MyReport.share", "xava.keyProperty=name");
		assertNoErrors();
		assertDialog();
		assertValidValues("name", new String[][] { 
			{"This is an report to share__SHARED_REPORT__",
			"This is an report to share (Shared)"}
		});
		
		// delete
		execute("MyReport.remove", "xava.keyProperty=name");
		assertNoErrors();
		assertMessage("Report 'This is an report to share' removed");
		assertEditable("name");
		assertNoAction("MyReport.createNew");
		assertNoAction("MyReport.remove");
		assertNoAction("MyReport.share");
	}
		
}