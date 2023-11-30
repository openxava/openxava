/**
 * 
 */
package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */
public class CustomerSellerInSectionTest extends ModuleTestBase {
	
	public CustomerSellerInSectionTest(String nameTest) {
		super(nameTest, "CustomerSellerInSection");
	}
	
	public void testRecursiveCollectionFromAReferenceInsideSection() throws Exception {
		execute("List.viewDetail", "row=0");
		assertValue("name", "Javi");
		assertValueInCollection("seller.customers", 1, 1, "Juanillo");
		execute("Collection.view", "row=1,viewObject=xava_view_section0_seller_customers");
		assertDialog();
		assertValue("name", "Juanillo");
		// In the current implementations remove the seller completely to avoid recursion
		// although we could improve it to remove only the customers collection inside seller
		// Even we could show the collection if find a way to not read the metadata recursively
		// but allows the user work recursively until the infinite
		// So the below assert, though true currently could be false and it would still better
		// assertNotExists("seller.customers");
	}
	
}
