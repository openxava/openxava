package org.openxava.test.actions;

import org.openxava.util.*;
import org.openxava.web.editors.*;

/** 
 * @author Jeromy Altuna
 */
public class ChooseFileAction extends org.openxava.actions.ChooseFileAction {
	
	@Override
	public void execute() throws Exception {
		String fileId = getView().getValueString(getNewFileProperty());
		if(!Is.emptyString(fileId)) 
			FilePersistorFactory.getInstance().remove(fileId);		
		super.execute();
	}
}
