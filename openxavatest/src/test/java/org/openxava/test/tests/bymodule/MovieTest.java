package org.openxava.test.tests.bymodule;

import java.util.*;
import java.util.stream.*;

import org.htmlunit.*;
import org.htmlunit.html.*;
import org.openxava.jpa.*;
import org.openxava.tests.*;
import org.openxava.util.*;
import org.openxava.web.editors.*;

/** 
 * 
 * @author Jeromy Altuna
 * @author Javier Paniza
 */
public class MovieTest extends MovieBaseTest { 
	
	private static final String MIME_UNKNOWN = "application/octet-stream";
	
	public MovieTest(String testName) {
		super(testName, "Movie"); 
	}
	
	public void testPdfConcatReport() throws Exception {
		assertListRowCount(3);
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
		if (System.getProperty("java.version").startsWith("1.8")) return; // Fails with Java 8 because it not recognized webm, we tolerate it by now, we'll fix it on demand.
		assertListRowCount(3);
		WebResponse response = getWebClient().getPage(
				    getUrlToFile("Forrest Gump Trailer.webm")).getWebResponse();
		assertTrue(response.getContentType().equals("video/webm") || // Fails with Java 8 because it not recognized webm, we tolerate it by now, we'll fix it on demand. 
				   response.getContentType().equals(MIME_UNKNOWN));
	}
		
	public void testDeleteFile_accessTrackerFile() throws Exception {
		long initialFilesCount = filesCount(); 
		addFile();
		assertEquals(initialFilesCount + 1, filesCount()); 
		assertTrue("Trailer has no value", !Is.emptyString(getValue("trailer")) && !"null".equals(getValue("trailer")));
		String entityId = getHtmlPage().getUrl().toString().split("=")[1]; 
		String fileId = getValue("trailer"); 
		removeFile("trailer");
		saveAndReloadMovie("JUNIT");
		assertEquals(initialFilesCount, filesCount()); 
		assertTrue("Trailer has value", Is.emptyString(getValue("trailer")) || "null".equals(getValue("trailer")));
		removeFile();
		
		LogTrackerUtils.assertAccessLog(  
			"MODIFIED: user=admin, model=Movie, key={}, changes=Trailer: File Corporation.html uploaded --> " + fileId,
			"CREATED: user=admin, model=Movie, key={id=" + entityId + "}",
			"CONSULTED: user=admin, model=Movie, key={id=" + entityId + "}",
			"CONSULTED: user=admin, model=Movie, key={id=" + entityId + "}",
			"MODIFIED: user=admin, model=Movie, key={id=" + entityId + "}, changes=Trailer: File Corporation.html removed --> " + fileId,
			"MODIFIED: user=admin, model=Movie, key={id=" + entityId + "}, changes=Trailer: " + fileId + " --> ", 
			"CONSULTED: user=admin, model=Movie, key={id=" + entityId + "}",
			"REMOVED: user=admin, model=Movie, key={id=" + entityId + "}",
			"CONSULTED: user=admin, model=Movie, key={id=ff80818150176f470150176ff2cb0000}" 
		);
	}
	
	public long filesCount() {
		return (Long) XPersistence.getManager()
			.createQuery("select count(*) from AttachedFile")
			.getSingleResult();
	}
	
	public void testFileset() throws Exception {
		subscribeToEmailNotifications(); 
		
		assertListRowCount(3);
		execute("List.viewDetail", "row=0");
		assertFilesCount("scripts", 3); 
		
		//Adding one file
		uploadFile("scripts", "src/main/resources/reports/Corporation.html"); 
		reload();
		
		assertFilesCount("scripts", 4); 
		
		//Display file
		int index = indexOfFile("scripts", "Corporation.html"); 
		
		assertFile("scripts", index, "text/html"); 
		
		//Removing the file
		removeFile("scripts", index); 
		reload();
		assertFilesCount("scripts", 3); 

		assertEmailNotifications( 
			"MODIFIED: email=openxavatest1@getnada.com, user=admin, application=OpenXavaTest, module=Movie, permalink=http://localhost:8080" + getContextPath() + "modules/Movie?detail=ff80818145622499014562259e980003, changes=<ul><li data-property='scripts'><b>Scripts</b>: NEW FILES ADDED --> Corporation.html</li></ul>",
			"MODIFIED: email=openxavatest1@getnada.com, user=admin, application=OpenXavaTest, module=Movie, permalink=http://localhost:8080" + getContextPath() + "modules/Movie?detail=ff80818145622499014562259e980003, changes=<ul><li data-property='scripts'><b>Scripts</b>: FILE REMOVED --> Corporation.html</li></ul>"
		);
	}
	
	private int indexOfFile(String property, String fileName) throws Exception { 
		String propertyValue = getValue(property);
		Collection<AttachedFile> files = FilePersistorFactory.getInstance().findLibrary((String) propertyValue);
		return files.stream().map(AttachedFile::getName).collect(Collectors.toList()).indexOf(fileName);
	}
	
	public void testGroupName() throws Exception {
		String groupId = Strings.removeBlanks("data sheet");
		
		assertListRowCount(3); 
		execute("List.viewDetail", "row=0");
		String groupName = getHtmlPage().getElementById("ox_openxavatest_Movie__label_" + groupId)
										.asNormalizedText().trim(); 
		assertTrue("Incorrect group name", groupName.equals(Labels.get(groupId)));		
	}
	
	public void testSectionsNames() throws Exception {
		List<String> sn = new ArrayList<String>();
		sn.add(Labels.get(Strings.removeBlanks("Multimedia 1")));
		sn.add(Labels.get(Strings.removeBlanks("Multimedia 2")));
				
		assertListRowCount(3); 
		execute("List.viewDetail", "row=0");
		assertTrue("At most two sections", getSectionsNames().size() == 2);
		assertTrue("Incorrect sections names", sn.removeAll(getSectionsNames()) && sn.isEmpty());
	}	
	
	public void testFilterEmptyValues() throws Exception {
		assertListRowCount(3); 
		assertFalse(isNotVisibleConditionValue(2));
		assertFalse(isNotVisibleConditionValue(3));
		
		// Filter String
		setConditionComparators("=", "=", "empty_comparator");
        // execute("List.filter");		
		assertListRowCount(2); 
		assertValueInList(0, 0, "GATTACA");
		assertValueInList(1, 0, "NOVECENTO");
		assertTrue(isNotVisibleConditionValue(2)); 
		
		setConditionComparators("=", "=", "=");
		execute("List.filter");
		assertListRowCount(3); 
		assertFalse(isNotVisibleConditionValue(2));
		
		//Filter Date
		setConditionComparators("=", "=", "=", "empty_comparator");
		// execute("List.filter");
		assertListRowCount(2);
		assertValueInList(0, 0, "GATTACA");
		assertValueInList(1, 0, "NOVECENTO");
		assertTrue(isNotVisibleConditionValue(3));
		
		// Filter keeping the value
		setConditionValues("", "", "f");
		setConditionComparators("=", "=", "empty_comparator");
        // execute("List.filter");		
		assertListRowCount(2);
		assertValueInList(0, 0, "GATTACA");
		assertValueInList(1, 0, "NOVECENTO");
		assertTrue(isNotVisibleConditionValue(2));		
	}
	
	public void testFilterNotEmptyValues() throws Exception {
		assertListRowCount(3); 
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
		assertListRowCount(3); 
		assertFalse(isNotVisibleConditionValue(2));
		
		//Filter Date
		setConditionComparators("=", "=", "=", "not_empty_comparator");
		// execute("List.filter");
		assertListRowCount(1);
		assertValueInList(0, 0, "FORREST GUMP");
		assertTrue(isNotVisibleConditionValue(3));
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
				sn.add(e.asNormalizedText().trim());
			}
		}
		return sn;
	}
	
	private boolean isNotVisibleConditionValue(int index) {
		String idConditionValue = "ox_" + getXavaJUnitProperty("application") + 
				                  "_Movie__conditionValue___" + index;
		HtmlElement input = getHtmlPage().getHtmlElementById(idConditionValue); 			
		return input.getAttribute("class").contains("ox-display-none"); 
	}
	
}