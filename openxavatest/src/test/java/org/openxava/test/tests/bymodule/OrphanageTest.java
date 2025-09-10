package org.openxava.test.tests.bymodule;

import java.text.*;
import java.util.*;

import org.openxava.test.model.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class OrphanageTest extends ModuleTestBase {
	
	public OrphanageTest(String testName) {
		super(testName, "Orphanage");
	}
	
	public void testGeneratePDFForACollectionInsideAGroup_dateFormatUsesClientLocale_generatePdfNotReloadEntity() throws Exception { 
		execute("List.viewDetail", "row=0");
		assertCollectionColumnCount("orphans", 1); 
		execute("Print.generatePdf", "viewObject=xava_view_orphanage_orphans"); 
		assertNoErrors();
		assertContentTypeForPopup("application/pdf"); 
		assertPopupPDFLinesCount(7);
		assertPopupPDFLine(1, "Orphans of Orphanage: EL INTERNADO");
		assertPopupPDFLine(3, "Name"); 
		assertPopupPDFLine(4, "JUAN");
		assertPopupPDFLine(5, "ANTONIO");
		
		setValue("name", "THE BOARDING SCHOOL"); 
		
		// print only the selected
		checkRowCollection("orphans", 1);
		execute("Print.generatePdf", "viewObject=xava_view_orphanage_orphans");
		assertNoErrors();
		assertContentTypeForPopup("application/pdf");
		assertPopupPDFLinesCount(6);
		assertPopupPDFLine(1, "Orphans of Orphanage: THE BOARDING SCHOOL"); 
		assertPopupPDFLine(3, "Name");
		assertPopupPDFLine(4, "ANTONIO");
		
		// Date formatted in English. It's only tested when the server use a non-English locale
		assertPopupPDFLine(5, "Page 1 of 1" + getCurrentDateInEnglish()); 
		
		assertValue("name", "THE BOARDING SCHOOL"); 
	}
	
	public void testOrphanRemovalAsEmbedded() throws Exception {
		assertEquals(2, Orphan.count());
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("orphans", 2);
		execute("Collection.new", "viewObject=xava_view_orphanage_orphans");
		setValue("name", "PEDRO");
		execute("Collection.save");
		assertEquals(3, Orphan.count());
		assertCollectionRowCount("orphans", 3);
		execute("Collection.removeSelected", "row=2,viewObject=xava_view_orphanage_orphans");
		assertCollectionRowCount("orphans", 2);
		assertEquals(2, Orphan.count());
		assertMessage("Orphan deleted from database");
	}
	
	public void testXavaEditorInCustomView() throws Exception {
		execute("List.viewDetail", "row=0");
		execute("Orphanage.proposeName");
		assertValue("name", "EL INTERNADO");
		setValue("name", "THE ORPHANAGE VII");
		execute("ProposeName.propose");
		assertMessage("I think that THE ORPHANAGE VII is already a good name"); 
	}
	
	private String getCurrentDateInEnglish() {
		return DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.ENGLISH).format(new Date());
	}
	
}
