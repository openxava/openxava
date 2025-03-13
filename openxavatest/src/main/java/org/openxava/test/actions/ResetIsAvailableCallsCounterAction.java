package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.util.*;

/**
 * Action to reset the counter for isAvailable() method calls.
 * 
 * @author Javier Paniza
 */
public class ResetIsAvailableCallsCounterAction extends ViewBaseAction {
	
	@Override
	public void execute() throws Exception {
		StaticCounter.reset();
		addMessage("isAvailable() counter has been reset to 0");
	}
}
