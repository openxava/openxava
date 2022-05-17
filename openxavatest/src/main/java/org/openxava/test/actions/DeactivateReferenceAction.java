package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class DeactivateReferenceAction extends ViewBaseAction {
		
	private String reference;

	public void execute() throws Exception {
		getView().setEditable(getReference(), false);
	}

	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	
}
