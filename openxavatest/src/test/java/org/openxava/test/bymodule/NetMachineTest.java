package org.openxava.test.bymodule;

import java.util.*;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */
public class NetMachineTest extends ModuleTestBase {

	public NetMachineTest(String testName) {
		super(testName, "NetMachine");
	}
	
	public void testMAC() throws Exception {
		setValue("name", "JUNIT");
		setValue("mac", "1");
		execute("CRUD.save");
		assertError("Invalid MAC");
		setValue("mac", "00:0D:61:2A:CB:B0");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertListRowCount(1);
		execute("CRUD.deleteRow", "row=0");
		assertListRowCount(0);
	}
	
	public void testNullsNotInList() throws Exception {
		NetMachine nm = new NetMachine();
		nm.setName("WITH NULL MAC");
		XPersistence.getManager().persist(nm);
		XPersistence.commit();
		execute("Mode.list"); 
		assertListRowCount(1);
		assertValueInList(0, 0, "WITH NULL MAC");
		assertValueInList(0, 1, "");
		
		execute("Print.generateExcel"); 
		assertContentTypeForPopup("text/x-csv");
		StringTokenizer excel = new StringTokenizer(getPopupText(), "\n\r");
		String header = excel.nextToken(); 
		assertEquals("header", "Name;Mac", header);
		String line1 = excel.nextToken();
		assertEquals("line1", "\"WITH NULL MAC\";", line1);
		assertTrue("Only one line must have generated", !excel.hasMoreTokens());

		execute("CRUD.deleteRow", "row=0");
		assertListRowCount(0);
	}
	
}
