package org.openxava.test.tests;

import java.util.*;

import org.openxava.jpa.*;
import org.openxava.util.*;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;

/** 
 * 
 * @author Jeromy Altuna
 * @author Javier Paniza
 */
public class MovieTest extends EmailNotificationsTestBase {
	
	private static final String MIME_UNKNOWN = "application/octet-stream";
	
	public MovieTest(String testName) {
		super(testName, "Movie");
	}
	
	public void testPdfConcatReport() throws Exception {
		assertListRowCount(2);
		execute("List.viewDetail", "row=0");
		assertAction("Movie.printDatasheet");
		assertValue("title", "FORREST GUMP");
		execute("Movie.printDatasheet");
		assertNoErrors();
		assertContentTypeForPopup("application/pdf"); 
		assertTrue(getPopupPDFAsText().indexOf("FORREST GUMP")>=0);
		assertTrue(getPopupPDFPageCount() == 3);
	}
	
	public void testClickOnFileInListMode() throws Exception {
		assertListRowCount(2);
		WebResponse response = getWebClient().getPage(
				    getUrlToFile("Forrest Gump Trailer.webm")).getWebResponse(); 
		assertTrue(response.getContentType().equals("video/webm") || 
				   response.getContentType().equals(MIME_UNKNOWN));
	}
	
	public void testAddFile() throws Exception {
		addFile();
		assertFile("trailer", "text/html"); 
		removeFile(); 
	}
	
	public void testAddFilesInNewEntity() throws Exception { 
		execute("CRUD.new");
		setValue("title", "MATRIX");
		uploadFile("scripts", "reports/Corporation.html");
		saveAndReloadMovie("MATRIX");
		assertFilesCount("scripts", 1);
		assertFile("scripts", 0, "text/html");
		
		removeFile("scripts", 0);
		execute("CRUD.delete");
	}
	
	
	public void testDeleteFile() throws Exception {
		addFile();
		assertTrue("Trailer has no value", !Is.emptyString(getValue("trailer")) && !"null".equals(getValue("trailer")));
		removeFile("trailer"); 
		saveAndReloadMovie("JUNIT");
		assertTrue("Trailer has value", Is.emptyString(getValue("trailer")) || "null".equals(getValue("trailer")));
		removeFile(); 
	}
	
	public void testFileset() throws Exception {
		subscribeToEmailNotifications(); 
		
		assertListRowCount(2);
		execute("List.viewDetail", "row=0");
		assertFilesCount("scripts", 3); 
		
		//Adding one file
		uploadFile("scripts", "reports/Corporation.html"); 
		reload();
		assertFilesCount("scripts", 4); 
		
		//Display file
		assertFile("scripts", 0, "text/html"); // 0 is the last added because of oid generation  
		
		//Removing the file
		removeFile("scripts", 0);
		reload();
		assertFilesCount("scripts", 3); 

		assertEmailNotifications(
			"MODIFIED: email=openxavatest1@getnada.com, user=admin, application=OpenXavaTest, module=Movie, permalink=http://localhost:8080/OpenXavaTest/modules/Movie?detail=ff80818145622499014562259e980003, changes=<ul><li><b>Scripts</b>: NEW FILES ADDED --> Corporation.html</li></ul>",
			"MODIFIED: email=openxavatest1@getnada.com, user=admin, application=OpenXavaTest, module=Movie, permalink=http://localhost:8080/OpenXavaTest/modules/Movie?detail=ff80818145622499014562259e980003, changes=<ul><li><b>Scripts</b>: FILE REMOVED --> Corporation.html</li></ul>"
		);
	}
	
	public void testGroupName() throws Exception {
		String groupId = Strings.removeBlanks("data sheet");
		
		assertListRowCount(2);
		execute("List.viewDetail", "row=0");
		String groupName = getHtmlPage().getElementById("ox_OpenXavaTest_Movie__label_" + groupId)
										.asText().trim(); 
		assertTrue("Incorrect group name", groupName.equals(Labels.get(groupId)));		
	}
	
	public void testSectionsNames() throws Exception {
		List<String> sn = new ArrayList<String>();
		sn.add(Labels.get(Strings.removeBlanks("Multimedia 1")));
		sn.add(Labels.get(Strings.removeBlanks("Multimedia 2")));
				
		assertListRowCount(2);
		execute("List.viewDetail", "row=0");
		assertTrue("At most two sections", getSectionsNames().size() == 2);
		assertTrue("Incorrect sections names", sn.removeAll(getSectionsNames()) && sn.isEmpty());
	}	
	
	public void testFilterEmptyValues() throws Exception {
		assertListRowCount(2);
		assertFalse(isNotVisibleConditionValue(2));
		assertFalse(isNotVisibleConditionValue(3));
		
		// Filter String
		setConditionComparators("=", "=", "empty_comparator");
        // execute("List.filter");		
		assertListRowCount(1);
		assertValueInList(0, 0, "NOVECENTO"); 
		assertTrue(isNotVisibleConditionValue(2));
		
		setConditionComparators("=", "=", "=");
		execute("List.filter");
		assertListRowCount(2);
		assertFalse(isNotVisibleConditionValue(2));
		
		//Filter Date
		setConditionComparators("=", "=", "=", "empty_comparator");
		// execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, 0, "NOVECENTO"); 
		assertTrue(isNotVisibleConditionValue(3));
		
		// Filter keeping the value
		setConditionValues("", "", "f");
		setConditionComparators("=", "=", "empty_comparator");
        // execute("List.filter");		
		assertListRowCount(1);
		assertValueInList(0, 0, "NOVECENTO"); 
		assertTrue(isNotVisibleConditionValue(2));		
	}
	
	public void testFilterNotEmptyValues() throws Exception {
		assertListRowCount(2); 
		assertFalse(isNotVisibleConditionValue(2));
		assertFalse(isNotVisibleConditionValue(3));
		
		// Filter String
		setConditionComparators("=", "=", "not_empty_comparator");
		// execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, 0, "FORREST GUMP");
		assertTrue(isNotVisibleConditionValue(2));		
		
		setConditionComparators("=", "=", "=");
		execute("List.filter");
		assertListRowCount(2);
		assertFalse(isNotVisibleConditionValue(2));
		
		//Filter Date
		setConditionComparators("=", "=", "=", "not_empty_comparator");
		// execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, 0, "FORREST GUMP");
		assertTrue(isNotVisibleConditionValue(3));
	}
	
	private void addFile() throws Exception {
		execute("CRUD.new");
		setValue("title", "JUNIT");
		uploadFile("trailer", "reports/Corporation.html"); 
		saveAndReloadMovie("JUNIT");
	}
	
	private void removeFile() throws Exception { 
		execute("CRUD.delete");
		XPersistence.getManager()
			.createQuery("delete from AttachedFile where name = 'Corporation.html'")
			.executeUpdate();
	}
	
	private void saveAndReloadMovie(String title) throws Exception { 
		execute("CRUD.save");
		execute("Mode.list");
		assertValueInList(0, 0, title);
		execute("List.viewDetail", "row=0");
		assertValue("title", title);		
	}
	
	private String getUrlToFile(String filename) { 
		String href = getFileAnchors().get(filename).getHrefAttribute();
		return "http://" + getHost() + ":" + getPort() + href;
	}
	
	private Map<String, HtmlAnchor> getFileAnchors() { 
		Map<String, HtmlAnchor> anchors = new HashMap<String, HtmlAnchor>(); 
		
		for(HtmlAnchor anchor : getHtmlPage().getAnchors()) {
			if(anchor.getHrefAttribute()
					 .indexOf("/xava/xfile?application=") >= 0)
			{
				anchors.put(anchor.getTextContent().trim(), anchor);
			}
		}		
		return anchors;
	}
	
	private List<String> getSectionsNames() {
		List<String> sn = new ArrayList<String>();
		for(DomElement e : getHtmlPage().getElementsByTagName("span")) {
			if(e.getAttribute("class").equals("ox-section-tab")) {
				sn.add(e.asText().trim());
			}
		}
		return sn;
	}
	
	private boolean isNotVisibleConditionValue(int index) {
		String idConditionValue = "ox_" + getXavaJUnitProperty("application") + 
				                  "_Movie__conditionValue___" + index;
		HtmlElement input = getHtmlPage().getHtmlElementById(idConditionValue); 
		return input.getAttribute("style").contains("display: none");			
	}
	
}