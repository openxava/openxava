package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */

public class ShowProductListAction extends ViewBaseAction { 

	public void execute() throws Exception {
		getView().setHidden("familyList", false);
		getView().setHidden("productList", false);
	}

}
