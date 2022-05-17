package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */
public class ShowSelectedAuthorsAction extends CollectionBaseAction {
	
	public void execute() throws Exception {
		for (Map values: getMapsSelectedValues()) {
			addMessage(values.get("name") + ", " + values.get("sex"));
		}
	}

}
