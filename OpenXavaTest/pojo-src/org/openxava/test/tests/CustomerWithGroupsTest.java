/**
 * 
 */
package org.openxava.test.tests;

import javax.persistence.*;

import org.openxava.jpa.XPersistence;
import org.openxava.tests.ModuleTestBase;

/**
 * When keys are within groups.
 * 
 * @author Federico Alcantara
 *
 */
public class CustomerWithGroupsTest extends ModuleTestBase {
	public CustomerWithGroupsTest(String nameTest) {
		super(nameTest, "CustomerWithGroups");
	}
	
	public void testConsecutiveCreationWithKeyInsideGroupAndSeveralGroups() throws Exception { 
		Integer customerNumber = getMaxCustomerNumber() + 1;
		for (int index = 0; index < 3; index++) {
			createCustomer(customerNumber + index);
		}
		deleteCustomers(); 
	}
	
	private void deleteCustomers() { 
		Query query = XPersistence.getManager().createQuery("delete from Customer c where c.name like 'Customer As #%'");
		query.executeUpdate();
	}

	private void createCustomer(Integer customerNumber) throws Exception {
		execute("CRUD.new");
		assertNoErrors();
		setValue("number", customerNumber.toString());
		setValue("name", "Customer As #" + customerNumber.toString());
		setValue("address.street", customerNumber.toString() + " Roundabout street");
		setValue("address.zipCode", "33420");
		setValue("address.city", "Coral Gables");
		setValue("address.state.id", "FL");
		execute("CustomerWithGroups.save");
		assertNoErrors();
		assertMessage("Customer created successfully");
	}
	
	private Integer getMaxCustomerNumber() throws Exception {
		Number value = (Number) XPersistence.getManager().createQuery("select max(number) from Customer")
			.getSingleResult();
		return value.intValue();
	}
}
