package org.openxava.test.tests;

import java.util.prefs.*;

import javax.persistence.*;

import org.openxava.jpa.*;
import org.openxava.tests.*;
import org.openxava.util.*;

import com.gargoylesoftware.htmlunit.html.*;

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
	
	public void testAttachFilesFromEmbeddableClass() throws Exception {
		setValue("name", "ANATOLY KARPOV");
		setValue("nickname.nickname", "POSITIONAL GAMER II");
		execute("CRUD.save");
		assertMessage("User with nickname created successfully");
		execute("Mode.list");
		execute("List.viewDetail", "row=0");
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
		HtmlAnchor icon = getHtmlPage().getAnchorByHref(
						"javascript:openxava.hideFrame('" + getFrameId() + "')");
		icon.click();
		waitView();
	}
	
	private void openFrame() throws Exception {
		HtmlAnchor icon = getHtmlPage().getAnchorByHref(
						"javascript:openxava.showFrame('" + getFrameId() + "')");
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
		execute("AttachedFile.choose", "newFileProperty=attachments.photo");
		assertNoErrors();
		String filepath = System.getProperty("user.dir") + "/test-images/foto_javi.jpg";
		setFileValue("newFile", filepath);
		execute("UploadFile.uploadFile");
		assertNoErrors();
		
		execute("AttachedFiles.add", "newFilesetProperty=attachments.documents");
		assertDialogTitle("Add files");
		filepath  = System.getProperty("user.dir") + "/reports/Corporation.html";
		setFileValue("newFile", filepath);
		execute("UploadFileIntoFileset.uploadFile");
		assertMessage("File added to Documents");
	}
	
	private void removeFiles() throws Exception {
		Query query = XPersistence.getManager()
								  .createQuery("delete from AttachedFile where " +
				                               "name=:photo or name=:document");
		query.setParameter("photo", "foto_javi.jpg");
		query.setParameter("document", "Corporation.html");
		assertTrue(query.executeUpdate() == 2);
		XPersistence.commit();
	}
}
