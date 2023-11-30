package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */
public class ComputerTest extends ModuleTestBase{

	public ComputerTest(String testName) {
		super(testName, "Computer");
	}
	
	public void testJoinedInheritanceStrategy() throws Exception {  
		assertListRowCount(3);
		
		assertValueInList(0, 0, "AS/400");
		assertValueInList(0, 1, "OS/400");
		
		assertValueInList(1, 0, "APP PC CLONE");
		assertValueInList(1, 1, "SUSE LINUX 11.2");

		assertValueInList(2, 0, "IPAD");
		assertValueInList(2, 1, "IOS 5");
		
		execute("List.viewDetail", "row=0");
		assertValue("name", "AS/400");
		assertValue("operatingSystem", "OS/400");
		assertNotExists("hardDiskCapacity");
		assertNotExists("screenSize");
		
		execute("Navigation.next");
		assertValue("name", "APP PC CLONE");
		assertValue("operatingSystem", "SUSE LINUX 11.2");
		assertValue("hardDiskCapacity", "130");
		assertNotExists("screenSize");

		execute("Navigation.next");
		assertValue("name", "IPAD");
		assertValue("operatingSystem", "IOS 5");
		assertNotExists("hardDiskCapacity");
		assertValue("screenSize", "10");
		
		changeModule("Desktop");
		assertListRowCount(1);
		assertValueInList(0, 0, "APP PC CLONE");
		assertValueInList(0, 1, "SUSE LINUX 11.2");
		assertValueInList(0, 2, "130");
		
		changeModule("Tablet");
		assertListRowCount(1);
		assertValueInList(0, 0, "IPAD");
		assertValueInList(0, 1, "IOS 5");
		assertValueInList(0, 2, "10");
	}
	
}
