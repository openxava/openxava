package org.openxava.actions;

import java.util.*;

import org.openxava.view.*;

public class RemoveFromReferenceAction extends ReferenceBaseAction {
	
	@Override
	public void execute() throws Exception {
		super.execute();
		View reference = getReferenceSubview();
		Map<?, ?> values = reference.getValues();
		values.replaceAll( (k,v)->v=null);
		reference.setValuesNotifying(values);
	}

}
