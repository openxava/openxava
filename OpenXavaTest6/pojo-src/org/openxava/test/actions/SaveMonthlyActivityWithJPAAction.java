package org.openxava.test.actions;



import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javier Paniza
 */
public class SaveMonthlyActivityWithJPAAction extends ViewBaseAction { 


	public void execute() throws Exception {
		MonthlyActivity activity = new MonthlyActivity();
		activity.setName(getView().getValueString("name"));
		XPersistence.getManager().persist(activity);
		
	} 

}
