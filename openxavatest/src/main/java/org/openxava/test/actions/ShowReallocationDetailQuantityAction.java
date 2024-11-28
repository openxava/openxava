package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;

public class ShowReallocationDetailQuantityAction extends ViewBaseAction {

	@Override
	public void execute() throws Exception {
		Reallocation reallocation = (Reallocation) getView().getEntity();
		System.out.println(reallocation.getDetails().size());
	}

}
