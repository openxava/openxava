package org.openxava.test.tests.bymodule;

import java.nio.charset.*;

import org.htmlunit.html.*;
import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class ReportTest extends ModuleTestBase {
	
		
	public ReportTest(String testName) {
		super(testName, "Report");		
	}
	
	public void testChangeViewNameToASubview_utf8CharactersInNonUtf8ByDefaultMachine() throws Exception {
		// Changing view name to a subview works, but it's not
		// efficient in AJAX terms, beacuse all root view is reloaded
		// This can be optimized
		execute("CRUD.new");
		assertExists("ranges.numberFrom");
		assertExists("ranges.numberTo");
		assertExists("ranges.dateFrom");
		assertExists("ranges.dateTo");
		
		setValue("ranges.type", "1"); // NUMBERS
		assertExists("ranges.numberFrom");
		assertExists("ranges.numberTo");
		assertNotExists("ranges.dateFrom"); 
		assertNotExists("ranges.dateTo");

		setValue("ranges.type", "2"); // DATES
		assertNotExists("ranges.numberFrom");
		assertNotExists("ranges.numberTo");
		assertExists("ranges.dateFrom");
		assertExists("ranges.dateTo");		
		
		// UTF-8 characters are shown, even in a computer with ISO-8859-1 (or other) as default encoding
		setLocale("zh");
		execute("Mode.list");
		assertListRowCount(0);
		assertCharactersShown(getHtmlPage().getAnchorByHref("/openxavatest/m/Invoice?retainOrder=true"));
		execute("Print.generatePdf");
		assertCharactersShown((HtmlPage) getWebClient().getWebWindows().get(1).getEnclosedPage());
	}
	
	public void assertCharactersShown(DomNode node) {
		String nodeContent = new String(node.asNormalizedText().getBytes(Charset.forName("UTF-8")));
		assertFalse(nodeContent.startsWith("?"));		
	}
	
		
}
