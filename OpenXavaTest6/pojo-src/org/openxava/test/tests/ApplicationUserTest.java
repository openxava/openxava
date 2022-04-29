package org.openxava.test.tests;

import org.openxava.test.model.*;
import org.openxava.tests.*;

import static org.openxava.jpa.XPersistence.*;

/**
 *  
 * @author Jeromy Altuna
 */
public class ApplicationUserTest extends ModuleTestBase {
	
	private ApplicationUser user;
	private Nickname nickname;
		
	public ApplicationUserTest(String testName) {
		super(testName, "ApplicationUser");
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		createEntities(); 
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		removeEntities();		
	}
	
	public void testUniqueConstraintsMessages() throws Exception {
		execute("Mode.list"); 
		assertListRowCount(1);
		execute("CRUD.new");
		setValue("nic", "5634AB78");
		execute("CRUD.save");
		assertError("National identity card is already registered");
		setValue("nic", "6634AB76");
		setValue("name", "TIGRAN PETROSIAN");
		setValue("birthdate", "6/17/1929");
		setValue("sex", "0");
		execute("CRUD.save");
		assertError("Very coincident user data");
		setValue("name", "ANATOLY KARPOV");
		setValue("birthdate", "5/23/1951");
		setValue("sex", "0");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertListRowCount(2);
		execute("List.orderBy", "property=name");
		execute("List.viewDetail", "row=0");
		assertValue("name", "ANATOLY KARPOV");
		execute("Collection.new", "viewObject=xava_view_nicknames");
		setValue("nickname", "POSITIONALGAMER");
		execute("Collection.saveAndStay");
		assertError("The nickname is already registered");
		setValue("nickname", "POSITIONALGAMERII");
		execute("Collection.save");
		assertNoErrors();
		assertCollectionRowCount("nicknames", 1);
		assertValueInCollection("nicknames", 0, "nickname", "POSITIONALGAMERII");
		execute("CRUD.delete");
		assertMessage("Application user deleted successfully");
	}
	
	public void testUniqueConstraintsMessagesFromNickname() throws Exception {
		changeModule("Nickname");
		assertListRowCount(1); 
		execute("CRUD.new");
		setValue("nickname", "POSITIONALGAMER");
		execute("CRUD.save");
		assertError("The nickname is already registered");
		setValue("nickname", "POSITIONALGAMERII");
		execute("Reference.createNew", "model=ApplicationUser,keyProperty=user.nic");
		setValue("nic", "5634AB78");
		setValue("name", "TIGRAN PETROSIAN");
		setValue("birthdate", "6/17/1929");
		setValue("sex", "0");
		execute("NewCreation.saveNew");
		assertError("National identity card is already registered");
		setValue("nic", "6634AB76");
		setValue("birthdate", "6/17/1929");
		execute("NewCreation.saveNew");
		assertError("Very coincident user data");
		setValue("name", "ANATOLY KARPOV");
		setValue("birthdate", "5/23/1951");
		setValue("sex", "0");
		execute("NewCreation.saveNew");
		assertNoErrors(); 
		execute("CRUD.save");
		assertMessage("Nickname created successfully");
		execute("Mode.list");
		assertListRowCount(2);
		setConditionValues("POSITIONALGAMERII");
		execute("CRUD.deleteRow", "row=0");
		changeModule("ApplicationUser");
		execute("Mode.list"); 
		assertListRowCount(2);
		execute("List.orderBy", "property=name");
		execute("CRUD.deleteRow", "row=0");
		assertListRowCount(1);		
	}
	
	private void createEntities(){				
		user = new ApplicationUser();
		user.setNic("5634AB78");
		user.setName("TIGRAN PETROSIAN");
		user.setBirthdate(org.openxava.util.Dates.create(17,06,1929));
		user.setSex(ApplicationUser.Sex.MALE);
		
		nickname = new Nickname();
		nickname.setNickname("POSITIONALGAMER");
		nickname.setUser(user);
		
		getManager().persist(user);
		getManager().persist(nickname);
		commit();
	}
	
	private void removeEntities(){
		getManager().remove(getManager().merge(nickname));
		getManager().remove(getManager().merge(user));
		commit();
	}	
}
