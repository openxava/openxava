package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.jpa.*;

/**
 * 
 * @author Javier Paniza
 */
public class SaveElementInCollectionAbortingTransactionAction extends SaveElementInCollectionAction {
	
	public void execute() throws Exception {
		XPersistence.getManager().getTransaction().setRollbackOnly();
		super.execute();
	}

}
