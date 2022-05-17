package org.openxava.test.actions;

import org.openxava.actions.*;



/**
 * @author Javier Paniza
 */
public class VerifyEditablesProductAction extends ViewBaseAction {
	
	public void execute() throws Exception {
		if (getView().isEditable("description")) {
			// It is possible to put the literal message, although is better use
			// a id for look in i18n files
			addError("description is editable and not should be it");
		}
		if (!getView().isEditable("unitPrice")) {
			addError("unit price is not editable and should be it");
		}
	}

}
