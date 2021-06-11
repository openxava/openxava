package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;

public class SearchArtistAgeReadOnlyAction extends SearchByViewKeyAction {
	
	public void execute() throws Exception {
		Map key = getView().getKeyValues();
		getView().setViewName("AgeReadOnly");
		getView().setValues(key);
		super.execute();
	}

}
