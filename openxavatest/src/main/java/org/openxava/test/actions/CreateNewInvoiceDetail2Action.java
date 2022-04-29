package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */

public class CreateNewInvoiceDetail2Action extends CreateNewElementInCollectionAction {
		
	public void execute() throws Exception {	
		super.execute();
		getCollectionElementView().setHidden("familyList", true);
		getCollectionElementView().setHidden("productList", true);
		addActions("InvoiceDetail2.showProductList");
	}

}
