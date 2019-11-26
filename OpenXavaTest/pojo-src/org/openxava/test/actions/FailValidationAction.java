package org.openxava.test.actions;



import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */
public class FailValidationAction extends ViewBaseAction { // tmp ¿Este nombre?


	public void execute() throws Exception {
	    String id = getView().getValueString("id");
	    Parent parent = XPersistence.getManager().find(Parent.class, id);
		parent.failValidation();
	} 

}
