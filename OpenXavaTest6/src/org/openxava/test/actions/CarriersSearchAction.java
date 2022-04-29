package org.openxava.test.actions;

import java.util.HashMap;
import java.util.Map;
import org.openxava.actions.SearchByViewKeyAction;

/**
 * 
 * @author Miguel Angel Embuena
 */

public class CarriersSearchAction extends SearchByViewKeyAction {
	
	private static final long serialVersionUID = 1L;
	private static final String NAME = "TRES";
	
	protected Map getValuesFromView() throws Exception {
		Map values = new HashMap(super.getValuesFromView());		
		values.put("name", NAME);		
		return values;
	}

}
