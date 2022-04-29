package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.test.model.*;

/**
 * 
 * @author Javier Paniza
 */
public class ChangeToFilterBySubfamilyAndYearRecordAction extends ViewBaseAction {

	public void execute() throws Exception {
		FilterBySubfamilyAndYearRecord record = new FilterBySubfamilyAndYearRecord();
		record.setNumber(1);
		record.setDescription("ONE");
		getView().setModel(record);
	}

}
