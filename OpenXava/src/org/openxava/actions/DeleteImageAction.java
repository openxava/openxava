package org.openxava.actions;


/**
 * Created on 04/02/2009 (16:23:01)
 * @autor Ana Andres
 */
public class DeleteImageAction extends ViewBaseAction{
	
	private String newImageProperty;
	
	public void execute() throws Exception {
		getView().setValue(getNewImageProperty(), null); 
	}

	public String getNewImageProperty() {
		return newImageProperty;
	}

	public void setNewImageProperty(String newImageProperty) {
		this.newImageProperty = newImageProperty;
	}

}
