package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class MoveRelationWithSellerToRemarksAction extends ViewBaseAction {

	public void execute() throws Exception {		
		Object rel = getView().getValue("relationWithSeller");		
		getView().setValue("remarks", rel);
	}

}
