package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */
public class SetTransientSellerUsingMapAction extends ViewBaseAction {

	public void execute() throws Exception {
		Map ref = Maps.toMap("number", 6, "name", "THE SIX"); 
		getView().setValue("transientSeller", ref);
	}

}
