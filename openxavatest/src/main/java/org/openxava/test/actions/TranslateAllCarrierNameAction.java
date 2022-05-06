package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.test.model.*;


/**
 * 
 * @author Javier Paniza
 */

public class TranslateAllCarrierNameAction extends ViewBaseAction {

	public void execute() throws Exception {
		for (Iterator it=Carrier.findAll().iterator(); it.hasNext(); ) {
			Carrier c = (Carrier) it.next();
			c.translate();
		}
		getView().refreshCollections();
	}

}
