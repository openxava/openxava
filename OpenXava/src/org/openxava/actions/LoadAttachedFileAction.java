package org.openxava.actions;

import java.util.*;

import org.apache.commons.fileupload.*;
import org.openxava.util.*;
import org.openxava.web.editors.*;

/**
 * Logic of AttachedFileEditor.load action in default-controllers.xml. <p>
 * 
 * Instance {@link org.openxava.web.editors.AttachedFile} object and stores it 
 * in a file container. <p>
 * 
 * @author Jeromy Altuna
 * @author Javier Paniza
 */
public class LoadAttachedFileAction extends AttachedFileBaseAction implements IProcessLoadedFileAction {
	
	@SuppressWarnings("rawtypes")
	private List fileItems;	
		
	public void execute() throws Exception {
		Iterator<?> it = getFileItems().iterator();
		while (it.hasNext()) {
			FileItem fi = (FileItem) it.next();					
			if (!Is.emptyString(fi.getName())) {
				AttachedFile file = new AttachedFile();
				file.setName(fi.getName());
				file.setData(fi.get());
				FilePersistorFactory.getInstance().save(file);
				getView().setValue(getProperty(), file.getId()); 
				trackModification(file, "uploaded_to_file_property"); 
				break;
			}			
		}		
	}
		
	@SuppressWarnings("rawtypes")
	public List getFileItems() {
		return fileItems;
	}

	@SuppressWarnings("rawtypes")
	public void setFileItems(List fileItems) {
		this.fileItems = fileItems; 
	}
	
}
