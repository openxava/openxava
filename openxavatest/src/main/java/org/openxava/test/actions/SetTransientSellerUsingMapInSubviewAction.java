package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class SetTransientSellerUsingMapInSubviewAction extends ViewBaseAction {

	public void execute() throws Exception {
		Map ref = Maps.toMap("number", 7, "name", "THE SEVEN"); 
		getView().getSubview("transientSeller").setValues(ref); // In subview to test a case
	}

}
