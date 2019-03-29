package org.openxava.actions;

import org.openxava.util.*;

/**
 * 
 * @since 5.9
 * @author Javier Paniza
 */

abstract public class FilterTabBaseAction extends TabBaseAction implements IChainActionWithArgv { 
	
	public String getNextAction() throws Exception { 
		return Is.emptyString(getCollection())?"ListFormat.select":null;
	}

	public String getNextActionArgv() throws Exception { 
		return "editor=" + getTab().getEditor();
	}

}
