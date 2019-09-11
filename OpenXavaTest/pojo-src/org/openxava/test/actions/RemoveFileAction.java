package org.openxava.test.actions;

import org.openxava.actions.*;
import org.openxava.web.editors.*;

/**
 * tmp Eliminar
 * @author Jeromy Altuna
 */
public class RemoveFileAction extends RemoveAttachedFileAction {
	
	@Override
	public void execute() throws Exception {
		/* tmp
		String fileId = getView().getValueString(getNewFileProperty());
		FilePersistorFactory.getInstance().remove(fileId);
		*/
		super.execute();		
	}
}
