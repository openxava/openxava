package org.openxava.actions;

import java.util.*;

import org.openxava.view.*;

public class ClearReferenceAction extends ReferenceBaseAction {
	
	@Override
	public void execute() throws Exception {
		super.execute();
		View referenceView = getReferenceSubview();
		View view = getView();
		Map<?, ?> values = referenceView.getKeyValues();
		values.replaceAll( (k,v)->v=null);
		view.setFocus(referenceView.getMemberName() + "." + referenceView.getSearchKeyName());
		referenceView.setValuesNotifying(values);
	}

}
