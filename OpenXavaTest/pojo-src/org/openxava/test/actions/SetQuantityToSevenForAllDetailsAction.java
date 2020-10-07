package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.test.model.*;

/**
 * tmp
 * 
 * @author Javir Paniza
 */
public class SetQuantityToSevenForAllDetailsAction extends ViewBaseAction {

	public void execute() throws Exception {
		Collection<Map> details = (Collection<Map>) getView().getValue("details");
		for (Map detail: details) {
			detail.put("quantity", 7);
		}
		getView().setValue("details", details);
	}

}
