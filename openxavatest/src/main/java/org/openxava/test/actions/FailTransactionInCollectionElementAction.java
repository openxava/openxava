package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.jpa.*;

/**
 * 
 * @author Javier Paniza
 */
public class FailTransactionInCollectionElementAction extends CollectionElementViewBaseAction {
	
	public void execute() throws Exception {
		XPersistence.getManager().getTransaction().setRollbackOnly();
		closeDialog();
	}

}
