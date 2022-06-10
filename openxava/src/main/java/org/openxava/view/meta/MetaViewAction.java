package org.openxava.view.meta;



import org.openxava.model.meta.*;
import org.openxava.util.*;

/**
 * Represents an action shown inside a view. <p>
 * 
 * It's a MetaProperty to be treated (in layout terms only) as an property. <p>
 * 
 * @author Javier Paniza
 */

public class MetaViewAction extends MetaProperty {

	private String action;
	private boolean alwaysEnabled;
	
	
	
	public MetaViewAction(String action) throws XavaException {
		this.action = action;
		setTypeName("java.lang.String");
		setName("__ACTION__" + Strings.change(this.action, ".", "_"));
		setStereotype("__ACTION__");
	}
	
	public String getAction() {
		return action;
	}

	public boolean isAlwaysEnabled() {
		return alwaysEnabled;
	}

	public void setAlwaysEnabled(boolean alwaysEnabled) {
		this.alwaysEnabled = alwaysEnabled;
	}

}
