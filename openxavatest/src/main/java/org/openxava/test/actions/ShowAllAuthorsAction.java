package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class ShowAllAuthorsAction extends CollectionBaseAction {
	
	public void execute() throws Exception {
		for (Map values: getMapValues()) {
			addMessage(values.get("name") + ", " + values.get("sex"));
		}
	}

}
