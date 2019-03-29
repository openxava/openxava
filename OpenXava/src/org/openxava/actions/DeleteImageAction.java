package org.openxava.actions;

import javax.inject.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created on 04/02/2009 (16:23:01)
 * @autor Ana Andres
 */
public class DeleteImageAction extends ViewBaseAction{
	private static Log log = LogFactory.getLog(DeleteImageAction.class);
	
	@Inject
	private String newImageProperty;
	
	public void execute() throws Exception {
		getView().setValue(getNewImageProperty(), new byte[0]); 
	}

	public String getNewImageProperty() {
		return newImageProperty;
	}

	public void setNewImageProperty(String newImageProperty) {
		this.newImageProperty = newImageProperty;
	}

}
