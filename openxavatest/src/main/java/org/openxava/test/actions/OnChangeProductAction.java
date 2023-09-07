package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * TMR QUITAR, NO DEJAR ESTA CLASE
 * @author javi
 *
 */
public class OnChangeProductAction extends OnChangePropertyBaseAction {

	public void execute() throws Exception {
		getView().getSubview("product").findObject();
		getView().setValue("evaluation", "BUENA");
		getView().getRoot().refreshCollections();  
	}

}
