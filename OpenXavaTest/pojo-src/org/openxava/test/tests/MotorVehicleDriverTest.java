package org.openxava.test.tests;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

/**
 *
 * @author Jeromy Altuna
 */
public class MotorVehicleDriverTest extends ModuleTestBase {
	
    private MotorVehicle vehicle1, vehicle2;
	
	public MotorVehicleDriverTest(String testName){
		super(testName, "MotorVehicleDriver");		
	}
	
	public void testConstraintAnnotacionMesssage() throws Exception {
		execute("CRUD.new");
		setValue("name", "MAGALI AVILA");
		execute("CRUD.save");
		assertError("Driver MAGALI AVILA can not be registered: must approved the driving test");
		setValue("approvedDrivingTest", "true");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertListRowCount(1); 
		execute("List.viewDetail", "row=0");
		assertValue("name", "MAGALI AVILA");
		setValue("approvedDrivingTest", "false");
		execute("CRUD.save");	
		assertError("Driver MAGALI AVILA can not be registered: must approved the driving test");
		assertValue("name", "MAGALI AVILA");
		execute("CRUD.delete");
		assertNoErrors();
	}
	
	public void testNotCanAddVehicleNotRoadworthy() throws Exception {
		createVehicles(); 
		execute("CRUD.new");
		setValue("name", "MAGALI AVILA");
		setValue("approvedDrivingTest", "true");
		execute("CRUD.save");
		assertNoErrors();
		execute("Mode.list");
		assertListRowCount(1);
		execute("List.viewDetail", "row=0");
		assertValue("name", "MAGALI AVILA");		
		execute("Collection.add", "viewObject=xava_view_vehicles");
		assertListRowCount(2); 
		checkAll();
		execute("AddToCollection.add");
		assertError("AUTO plate L2-0002 is not roadworthy. It can not be assigned to the driver MAGALI AVILA");		
		assertCollectionRowCount("vehicles", 1);
		execute("Collection.removeSelected", "row=0,viewObject=xava_view_vehicles");
		assertMessage("Association between Motor vehicle and Motor vehicle driver has been removed, " + 
			"but Motor vehicle is still in database"); 
		execute("Mode.list");
		execute("CRUD.deleteRow", "row=0");
		assertNoErrors();
		assertListRowCount(0);
		removeVehicles();
	}
	
	private void createVehicles(){
		vehicle1 = new MotorVehicle();
		vehicle1.setType("AUTO");
		vehicle1.setLicensePlate("L1-0001");
		vehicle1.setRoadworthy(true);
		
		vehicle2 = new MotorVehicle();
		vehicle2.setType("AUTO");
		vehicle2.setLicensePlate("L2-0002");
		
		XPersistence.getManager().persist(vehicle1);
		XPersistence.getManager().persist(vehicle2);
		XPersistence.commit();  
	}
	
	private void removeVehicles(){
		XPersistence.getManager().remove(XPersistence.getManager().merge(vehicle1));
		XPersistence.getManager().remove(XPersistence.getManager().merge(vehicle2));
		XPersistence.commit();
	}		
}
