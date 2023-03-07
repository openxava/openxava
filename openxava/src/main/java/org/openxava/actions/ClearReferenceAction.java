package org.openxava.actions;

import java.util.*;

import org.openxava.view.*;

public class ClearReferenceAction extends ReferenceBaseAction {
	
	@Override
	public void execute() throws Exception {
		super.execute();
		View referenceView = getReferenceSubview();
		Map<?, ?> values = referenceView.getKeyValues();
		values.replaceAll( (k,v)->v=null);
		referenceView.setValuesNotifying(values);
	}

}
