package org.openxava.test.bymodule;

import java.util.*;
import java.util.prefs.*;

import javax.persistence.*;

import org.htmlunit.html.*;
import org.openxava.jpa.*;
import org.openxava.tests.*;
import org.openxava.util.*;

/**
 * 
 * @author Jeromy Altuna
 */
public class UserWithNicknameTest extends ModuleTestBase {
		
	private String frameId;
		
	public UserWithNicknameTest(String testName) {
		super(testName, "UserWithNickname");
		Users.setCurrent("admin");
	}
	
	public void testUniqueConstraintsMessages() throws Exception {
		execute("Mode.list"); 
		assertListRowCount(0);
		execute("CRUD.new");
		setValue("name", "TIGRAN PETROSIAN");
		setValue("nickname.nickname", "POSITIONAL GAMER");
		execute("CRUD.save");
		assertMessage("User with nickname created successfully");
		execute("CRUD.new");
		setValue("name", "VLADIMIR KRAMNIK");
		setValue("nickname.nickname", "POSITIONAL GAMER");
		execute("CRUD.save");
		assertError("The nickname is already registered"); 
		setValue("nickname.nickname", "POSITIONAL GAMER III");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertListRowCount(2);
		execute("List.viewDetail", "row=1");
		assertValue("name", "VLADIMIR KRAMNIK");
		setValue("nickname.nickname", "POSITIONAL GAMER");
		execute("CRUD.save");
		assertError("The nickname is already registered");		
		execute("Mode.list");
		checkAll();
		execute("CRUD.deleteSelected");
		assertNoErrors();
		assertListRowCount(0);
		
		removeNicknames();
	}
	
	public void testAttachFilesFromEmbeddableClass_catalanMessages() throws Exception {
		setValue("name", "ANATOLY KARPOV");
		setValue("nickname.nickname", "POSITIONAL GAMER II");
		execute("CRUD.save");
		assertMessage("User with nickname created successfully"); 
		execute("Mode.list");
		setLocale("ca");
		execute("List.viewDetail", "row=0");
		HtmlSpan span = getHtmlPage().getFirstByXPath("//span[@class='ox-editor-suffix']");
		List<DomNode> insideSpan = span.getChildNodes();
		assertEquals("El número u no s''esborrable", insideSpan.get(0).getNodeValue().toString().trim());
		attachFiles(); 		
		execute("CRUD.delete");
		assertNoErrors();
		removeNicknames();
		removeFiles(); 
	}	

	public void testStoreFrameStatusWithTooLongName() throws Exception {
		//Open frame		
		assertEquals(false, isClosedFrameInHtml()); 
		assertEquals(false, isClosedFrameInPreferences());		
		closeFrame(); 
		//Closed frame
		assertEquals(true, isClosedFrameInHtml());
		assertEquals(true, isClosedFrameInPreferences());
		
		openFrame(); 
	}
		
	private boolean isClosedFrameInPreferences() throws BackingStoreException {
		Preferences p = Users.getCurrentPreferences().node("view.UserWithNickname.."); 	
		p.sync();
		return p.getBoolean(getFrameKey(), false);
	}
	
	private boolean isClosedFrameInHtml() {
		HtmlElement span = getHtmlPage().getHtmlElementById(getFrameId() + "hide"); 
		return span.getAttribute("style").contains("display: none");	
	}
	
	private void closeFrame() throws Exception {
		HtmlAnchor icon = getFrameAnchor(getFrameId(), "xava_hide_frame"); 
		icon.click();
		waitView();
	}
	
	private HtmlAnchor getFrameAnchor(String frame, String cssClass) throws Exception {  
		for (HtmlElement anchor: getHtmlPage().getBody().getElementsByAttribute("a", "data-frame", getFrameId())) {
			if (cssClass.equals(anchor.getAttribute("class"))) return (HtmlAnchor) anchor; 
		} 
		throw new NoSuchElementException();
	}
	
	private void openFrame() throws Exception {
		HtmlAnchor icon = getFrameAnchor(getFrameId(), "xava_show_frame"); 
		icon.click();
		waitView();
	}
	
	private String getFrameKey() {
		return "frameClosed." + getFrameId().hashCode();
	}
	
	private String getFrameId() {
		if(frameId == null){
			frameId = "ox_" + getXavaJUnitProperty("application") + 
					  "_UserWithNickname__frame_listOfNicknamesThatAreUsedByOthersUsers";
		}
		return frameId;
	}
	
	private void waitView() {
		try {
			Thread.sleep(1000);
		} catch(InterruptedException e){}
	}
	
	private void removeNicknames() throws Exception {
		changeModule("Nickname");
		checkAll();
		execute("CRUD.deleteSelected");
	}
	
	private void attachFiles() throws Exception {
		uploadFile("attachments.photo", "test-images/foto_javi.jpg");
		execute("CRUD.save");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
		assertValue("name", "ANATOLY KARPOV");				
		assertFile("attachments.photo"); 
		
		uploadFile("attachments.documents", "src/main/resources/reports/Corporation.html"); 
		reload();
		assertFile("attachments.documents", 0, "text/html");
	}
	
	private void removeFiles() throws Exception {
		Query query = XPersistence.getManager()
		  .createQuery("delete from AttachedFile where " +
                       "name=:photo or name=:document");
		query.setParameter("photo", "foto_javi.jpg");
		query.setParameter("document", "Corporation.html");
		int count = query.executeUpdate();
		assertEquals(2, count);
		XPersistence.commit();
	}
}
