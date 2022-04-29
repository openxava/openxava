package org.openxava.test.tests;

import java.util.*;


/**
 * 
 * @author Javier Paniza
 */

public class IssueTest extends IssueTestBase {
	
	public IssueTest(String testName) {
		super(testName, "Issue", "");		
	}

	public void testCollectionWithConditionAndChangedSchema() throws Exception{
		// Company A
		assertValueInList(0, 0, "A0001"); 
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("comments", 1); 
		assertCollectionRowCount("commentsWithCondition", 1);
		assertValueInCollection("comments", 0, 1, "Comment on A0001");
		assertValueInCollection("commentsWithCondition", 0, 1, "Comment on A0001");
		
		// changed to Company B
		execute("Mode.list");
		execute("Issue.changeToCompanyB");
		assertValueInList(0, 0, "B0001"); 
		execute("List.viewDetail", "row=0");
		assertCollectionRowCount("comments", 1);
		assertCollectionRowCount("commentsWithCondition", 1);
		assertValueInCollection("comments", 0, 1, "Comment on B0001");
		assertValueInCollection("commentsWithCondition", 0, 1, "Comment on B0001");
	}
	
	public void testSearchReferenceWithDynamicChangeOfDefaultSchema() throws Exception {
		execute("CRUD.new");
		// Searching with list
		execute("Reference.search", "keyProperty=xava.Issue.worker.nickName");
		assertListRowCount(1);
		assertValueInList(0, 0, "JAVI"); 
		execute("ReferenceSearch.choose", "row=0");
		assertNoErrors();
		assertValue("worker.nickName", "JAVI");
		assertValue("worker.fullName", "FRANCISCO JAVIER PANIZA LUCAS");
		
		// Searching on change key
		setValue("worker.nickName", "");
		assertValue("worker.fullName", "");
		setValue("worker.nickName", "JAVI");
		assertValue("worker.nickName", "JAVI");
		assertValue("worker.fullName", "FRANCISCO JAVIER PANIZA LUCAS");		
	}
	
	public void testGenerateExcellWithTextThatContainsSemicolon() throws Exception {
		// create description whit semicolon
		execute("CRUD.new");
		setValue("id", "ABCDE");
		setValue("description", "UNO;\"DOS\";TRES");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		
		// filter by the new and generate csv
		setConditionValues(new String[] { "ABCDE" });
		execute("List.filter");
		assertListRowCount(1);
		execute("Print.generateExcel");
		assertContentTypeForPopup("text/x-csv");

		String expectedLine = "\"ABCDE\";\"UNO;\"\"DOS\"\";TRES\"";
		StringTokenizer excel = new StringTokenizer(getPopupText(), "\n\r");
		String header = excel.nextToken();
		assertEquals("header", "Id;Description", header); 		
		String line1 = excel.nextToken();
		assertEquals("line1", expectedLine, line1);
		assertTrue("Only one line must have generated", !excel.hasMoreTokens());
		
		// delete description with semicolon
		assertListRowCount(1);
		checkAll();
		execute("CRUD.deleteSelected");		
		assertNoErrors();
	}
	
	public void testRequiredOnReferenceToParent() throws Exception {
		execute("CRUD.new");
		setValue("id", "JUNIT");
		setValue("description", "JUNIT ISSUE");
		setValue("worker.nickName", "JAVI");
		assertValue("worker.fullName", "FRANCISCO JAVIER PANIZA LUCAS"); 
		assertCollectionRowCount("comments", 0);
		execute("Collection.new", "viewObject=xava_view_comments");
		setValue("comment", "Created from a JUNIT test");
		execute("Collection.save");
		assertNoErrors(); 
		assertCollectionRowCount("comments", 1);
		execute("CRUD.delete");
		assertNoErrors();
	}
		
}
