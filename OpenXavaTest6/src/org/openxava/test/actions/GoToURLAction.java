package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * 
 * @author Javier Paniza
 */

public class GoToURLAction extends BaseAction implements IForwardAction {
	
	private String url;
	private String property;
	private String viewObject;
	
	public void execute() throws Exception {							
		View view = (View) getContext().get(getRequest(), viewObject);
		url=view.getValueString(property);
		if (Is.emptyString(url)) {
			url = null;
			addError("cannot_go_to_empty_url");			
		}
	}	

	public String getForwardURI() {
		return url;
	}

	public boolean inNewWindow() {
		return false;
	}
	
	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getViewObject() {
		return viewObject;
	}

	public void setViewObject(String viewObject) {
		this.viewObject = viewObject;
	}
	
}
