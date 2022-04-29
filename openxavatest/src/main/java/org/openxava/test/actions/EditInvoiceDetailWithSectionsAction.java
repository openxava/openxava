package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class EditInvoiceDetailWithSectionsAction extends EditElementInCollectionAction {

	public void execute() throws Exception {
		super.execute();
		getCollectionElementView().setViewName("AllMembersInSections"); 
	}

}
