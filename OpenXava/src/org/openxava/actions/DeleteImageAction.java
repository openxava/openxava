package org.openxava.actions;


/**
 * Created on 04/02/2009 (16:23:01)
 * @autor Ana Andres
 */
public class DeleteImageAction extends ViewBaseAction{
	
	// tmp private String newImageProperty;
	private String property; // tmp
	
	public void execute() throws Exception {
		// tmp getView().setValue(getNewImageProperty(), null); 
		getView().setValue(property, null); // tmp
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	/* tmp
	public String getNewImageProperty() {
		return newImageProperty;
	}

	public void setNewImageProperty(String newImageProperty) {
		this.newImageProperty = newImageProperty;
	}
	*/

}
