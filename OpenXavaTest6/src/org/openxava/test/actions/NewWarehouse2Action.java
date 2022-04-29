package org.openxava.test.actions;

/**
* Create on 11 ene. 2017 (8:58:03)
* @author Ana Andres
*/
public class NewWarehouse2Action extends NewWarehouseAction{

	@Override
	public void execute() throws Exception {
		super.execute();
		
		addInfo("This is my action");
	}
	
}