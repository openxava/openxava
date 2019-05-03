package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */

public class JavaProgrammerTest extends ModuleTestBase {
	
	public JavaProgrammerTest(String testName) {
		super(testName, "JavaProgrammer");		
	}
		
	public void test2LevelsInheritedEntityCRUD() throws Exception { 
		execute("CRUD.new");
		setValue("name", "JUNIT JAVA PROGRAMMER");
		setValue("sex", "1");
		setValue("mainLanguage", "JAVA");
		setValue("favouriteFramework", "OPENXAVA");
		execute("CRUD.save");
		assertNoErrors();
		assertValue("name", "");
		assertValue("sex", "");
		assertValue("mainLanguage", "");
		assertValue("favouriteFramework", "");
		setValue("name", "JUNIT JAVA PROGRAMMER");
		execute("CRUD.refresh");
		assertValue("name", "JUNIT JAVA PROGRAMMER");
		assertValue("sex", "1");
		assertValue("mainLanguage", "JAVA");
		assertValue("favouriteFramework", "OPENXAVA");
		execute("CRUD.delete");
		assertMessage("Java programmer deleted successfully");
	}
	
	public void test2LevelsInheritedEntityWithBaseConditionList_noEmailSubscriptionsIfBaseCondition() throws Exception { 
		assertFalse(getHtml().contains("'" + EmailNotificationsTestUtil.getEmailSubscriptionAction() + "'")); // Because assertNoAction does not work for this action
		
		assertListColumnCount(6);  
		assertLabelInList(0, "Name");
		assertLabelInList(1, "Sex");
		assertLabelInList(2, "Author of Favorite author"); 
		assertLabelInList(3, "Biography of Favorite author");
		assertLabelInList(4, "Main language");
		assertLabelInList(5, "Favourite framework");
		assertListRowCount(1);
		assertValueInList(0, 0, "JAVI");  				
	}
		
}
