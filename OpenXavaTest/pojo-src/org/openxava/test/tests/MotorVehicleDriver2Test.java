package org.openxava.test.tests;

import org.openxava.tests.*;

/**
 *
 * @author Jeromy Altuna
 */
public class MotorVehicleDriver2Test extends ModuleTestBase {
	
	public MotorVehicleDriver2Test(String testName){
		super(testName, "MotorVehicleDriver2");
	}
	
	public void testNotCanAddVehicleNotRoadworthy() throws Exception { 
		execute("CRUD.new");
		setValue("name", "KAREN");
		setValue("approvedDrivingTest", "true");
		execute("Collection.new", "viewObject=xava_view_vehicles");
		setValue("type", "MOTORBIKE");
		setValue("licensePlate", "MO-0002");
		setValue("roadworthy", "false");
		execute("Collection.save");
		assertError("MOTORBIKE plate MO-0002 is not roadworthy. " + 
				    "It can not be assigned to the driver KAREN");
		setValue("roadworthy", "true");
		execute("Collection.save");
		assertMessagesCount(2);
		assertCollectionRowCount("vehicles", 1);
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_vehicles");
		assertMessage("Motor vehicle deleted from database");
		execute("CRUD.delete");
		assertMessage("Motor vehicle driver 2 deleted successfully");
		execute("Mode.list");
		assertListRowCount(0);
	}
}
