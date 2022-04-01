package org.openxava.actions;

/**
 * 
 * @author Javier Paniza 
 */
public class SetEditableAction extends ViewBaseAction {
	
	private boolean editable;
	private boolean keyEditable;

	public void execute() throws Exception {
		getView().setKeyEditable(keyEditable);
		getView().setEditable(editable);		
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setKeyEditable(boolean keyEditable) {
		this.keyEditable = keyEditable;
	}

	public boolean isKeyEditable() {
		return keyEditable;
	}

}
