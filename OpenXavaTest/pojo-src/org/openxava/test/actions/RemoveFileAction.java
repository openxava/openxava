package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.web.editors.*;

/**
 * @author Jeromy Altuna
 */
public class RemoveFileAction extends DeleteFileAction {
	
	@Override
	public void execute() throws Exception {
		String fileId = getView().getValueString(getNewFileProperty());
		FilePersistorFactory.getInstance().remove(fileId);
		super.execute();		
	}
}
