package org.openxava.actions;

import javax.inject.*;
import org.openxava.session.*;

/**
 * 
 * @author Javier Paniza
 */
public class RemoveMyReportColumnAction extends CollectionBaseAction {
	
	@Inject
	private MyReport myReport; 
	
	public void execute() throws Exception {
		for (Object o: getSelectedObjects()) {
			myReport.getColumns().remove(o);
		}
	}

}
