package org.openxava.test.tests;

import org.openxava.jpa.*;
import org.openxava.test.model.*;
import org.openxava.tests.*;

/**
 * @author Javier Paniza
 */

public class ShipmentChargeTest extends ModuleTestBase {
			
	public ShipmentChargeTest(String testName) {
		super(testName, "ShipmentCharge");		
	}
	
	public void testOverlappedReferencesWithConverterInANotOverlappedColumn() throws Exception {
		deleteShipmentCharges();
		// Creating
		execute("CRUD.new");
		setValue("mode", "0");
		String shipment = toKeyString(getShipment()); 
		setValue("shipment.KEY", shipment);
		setValue("amount", "150");
		execute("CRUD.save");
		assertNoErrors();  
		assertValue("shipment.KEY", "");
		
		// Verfiying in list
		execute("Mode.list");
		assertListRowCount(1);
		assertValueInList(0, "amount", "150.00"); 
		assertTotalInList("amount", "150.00");
		
		// Searching and verifying
		execute("List.viewDetail", "row=0");		
		assertValue("mode", "0"); 
		assertValue("shipment.KEY", shipment);
		assertValue("amount", "150.00");
	}
	
	public void testFilterToDescriptionsList() throws Exception {
		assertListRowCount(1); 
		assertLabelInList(2, "Shipment number"); 
		assertLabelInList(3, "Slow");	// fail filter: calculated property and descriptions list
		assertLabelInList(4, "Shipment"); 
		
		// reference property: descriptionsList		
		setConditionValues(new String[] { "", "", "", "CINC"} ); 
		execute("List.filter");
		assertListRowCount(0); 
		
		setConditionValues(new String[] { "", "", "", "UNO INTERNO"} ); 
		execute("List.filter");
		assertListRowCount(1);
		
		// reference property: normal
		setConditionValues(new String[] { "", "", "1"} );
		execute("List.filter");
		assertListRowCount(1);
		
		setConditionValues(new String[] { "", "", "10"} );
		execute("List.filter");
		assertListRowCount(0);
	}
	
	private void deleteShipmentCharges() {
		XPersistence.getManager().createQuery("delete from ShipmentCharge").executeUpdate();
		XPersistence.commit();
	}
	
	private Shipment getShipment() {
		return (Shipment) Shipment.findByMode(0).iterator().next();
	}
}
