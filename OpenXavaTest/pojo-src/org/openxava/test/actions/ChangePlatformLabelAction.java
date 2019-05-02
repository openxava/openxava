package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.util.*;

/** 
 * tmp
 * 
 * @author Javier Paniza
 */
public class ChangePlatformLabelAction extends BaseAction {
	
	public void execute() throws Exception {
		Labels.put("ProgrammerApplicant.platform", Locale.US, "Target ecosystem");	// Local.US to test that country is ignored, 
				// because some browsers send the country while others not.	
	}

}
