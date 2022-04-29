/**
 * 
 */
package org.openxava.test.tests;

import org.openxava.jpa.XPersistence;
import org.openxava.tests.ModuleTestBase;

/**
 * Test the behavior of embeddable classes
 * at more than 1 level
 * @author Federico Alcantara
 *
 */
public class SalesRepresentativeTest extends ModuleTestBase{  
	public SalesRepresentativeTest(String nameTest) {
		super(nameTest, "SalesRepresentative");
	}

	@Override
	public void setUp() throws Exception {
		removeData();
		super.setUp();
	}
	
	@Override
	public void tearDown() throws Exception {		
		super.tearDown();
		removeData();
	}
		
	/**
	 * Test if CRUD is performing okey.
	 * @throws Exception
	 */
	public void testTwoLevelsEmbedded() throws Exception { 
		assertNoErrors(); // This could happen
		execute("CRUD.new");
		assertNoErrors(); // This too
		
		assertViewSetHiddenFromAnEmbedded(); 
		
		setValue("repEmployeeNumber", "1");
		setValue("repCommissionRate", "3");
		setValue("person.personFirstName", "JOHN");
		setValue("person.personLastName", "DOE");
		setValue("person.phoneNumber.phoneAreaCode", "305");
		setValue("person.phoneNumber.phoneDigits", "555-1212");
		setValue("person.phoneNumber.phoneExtension", "999");
		execute("CRUD.save");
		assertNoErrors();
		
		// let's read from list mode
		execute("Mode.list");
		assertListRowCount(1);
		assertValueInList(0, "person.personLastName", "DOE");
		assertValueInList(0, "person.phoneNumber.phoneDigits", "555-1212");
		
		// let's read in detail mode
		execute("List.viewDetail", "row=0");
		assertValue("repEmployeeNumber", "1");
		assertValue("repCommissionRate", "3.00"); 
		assertValue("person.personFirstName", "JOHN");
		assertValue("person.personLastName", "DOE");
		assertValue("person.phoneNumber.phoneAreaCode", "305");
		assertValue("person.phoneNumber.phoneDigits", "555-1212");
		assertValue("person.phoneNumber.phoneExtension", "999");
		
		// Let's update it
		setValue("person.personFirstName", "JANE");
		execute("CRUD.save");
		assertNoErrors();
		
		// Let's delete it
		execute("CRUD.new");
		setValue("repEmployeeNumber", "1");
		execute("CRUD.refresh");
		assertValue("repEmployeeNumber", "1");
		assertValue("repCommissionRate", "3.00"); 
		assertValue("person.personFirstName", "JANE");
		assertValue("person.personLastName", "DOE");
		assertValue("person.phoneNumber.phoneAreaCode", "305");
		assertValue("person.phoneNumber.phoneDigits", "555-1212");
		assertValue("person.phoneNumber.phoneExtension", "999");
		execute("CRUD.delete");
		assertNoErrors();
		
		// Find out if erased
		execute("CRUD.new");
		setValue("repEmployeeNumber", "1");
		execute("CRUD.refresh");
		assertErrorsCount(1);
	}
	
	private void assertViewSetHiddenFromAnEmbedded() throws Exception { 
		assertExists("person.phoneNumber.phoneExtension");
		setValue("person.phoneNumber.phoneAreaCode", "34");
		assertNotExists("person.phoneNumber.phoneExtension");
		setValue("person.phoneNumber.phoneAreaCode", "");
		assertExists("person.phoneNumber.phoneExtension");
	}

	private void removeData() throws Exception {
		XPersistence.getManager().createQuery("delete from SalesRepresentative")
				.executeUpdate();
		XPersistence.commit(); 
	}
	
}
