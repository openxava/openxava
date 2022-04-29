package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */
public class FilterInvoice4ByDateAction extends TabBaseAction {

	public void execute() throws Exception {
		Date date = Dates.create(4, 1, 2004);
		getTab().setConditionValue("date", date); 
	}

}
