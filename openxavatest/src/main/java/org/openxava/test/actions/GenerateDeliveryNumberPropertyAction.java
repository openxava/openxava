package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.view.*;


/**
 * @author Javier Paniza
 */
public class GenerateDeliveryNumberPropertyAction extends BaseAction implements IPropertyAction {
	
    private View view;
    private String property;
 
    public void execute() throws Exception {
        view.setValue(property, new Integer(88));
    }
 
    public void setProperty(String property) {
        this.property = property;
    }
    public void setView(View view) {
        this.view = view;
    }
 
}