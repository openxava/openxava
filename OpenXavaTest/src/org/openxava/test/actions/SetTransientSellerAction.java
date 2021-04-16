package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */
public class SetTransientSellerAction extends ViewBaseAction {

	public void execute() throws Exception {
		getView().setValue("transientSeller.number", 3); // TMP PROBAR PIENDO LAS DOS SIN BUSCAR Y CONTRA LA SUBVISTA
		getView().getSubview("transientSeller").refresh();
	}

}
