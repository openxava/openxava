package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;

/**
 * tmp
 * @author Javier Paniza
 */

public class SearchArtistSomeMembersReadOnlyAction extends SearchByViewKeyAction {
	
	public void execute() throws Exception {
		Map key = getView().getKeyValues();
		getView().setViewName("SomeMembersReadOnly");
		getView().setValues(key);
		super.execute();
	}

}
