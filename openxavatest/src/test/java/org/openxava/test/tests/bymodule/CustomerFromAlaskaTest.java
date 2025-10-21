package org.openxava.test.tests.bymodule;

import org.openxava.tests.*;

/**
 * 
 * @author Javier Paniza
 */
public class CustomerFromAlaskaTest extends ModuleTestBase { 	
	
	public CustomerFromAlaskaTest(String testName) {
		super(testName, "CustomerFromAlaska");
	}
				
	public void testBaseConditionStartedByFrom_noEmailSubscriptionsIfBaseCondition() throws Exception {
		assertFalse(getHtml().contains("'" + EmailNotificationsUtils.getEmailSubscriptionAction() + "'")); // Because assertNoAction does not work for this action
		
		assertListRowCount(1);
		assertValueInList(0, "number", "43"); 
		assertValueInList(0, "name", "Gonzalo Gonzalez");

		// tmr Cambiar nombre de método
		// TMR ME QUEDÉ POR AQUÍ: CONSIDERAR HACERLO EN Product6, PORQUE ME AHORRO UN NEW
		execute("CRUD.new");
		setValue("type", "STEADY");
		setValue("name", "Javi");
		setValue("relationWithSeller", "");
		execute("CRUD.refresh");
		assertValue("number", "1");
		
	}
			
}
