package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.util.*;

/**
 * Action to show the current number of isAvailable() method calls.
 * 
 * @author Javier Paniza
 */
public class ShowIsAvailableCallsCounterAction extends ViewBaseAction {
	
	@Override
	public void execute() throws Exception {
		int count = StaticCounter.getValue();
		addMessage("isAvailable() has been called " + count + " times");
	}
}
