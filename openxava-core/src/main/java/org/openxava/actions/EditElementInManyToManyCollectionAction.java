package org.openxava.actions;

/**
 * Prepare a dialog to edit an element from a ManyToMany collection.
 * <p>
 * 
 * @author Franklin Alier
 */
public class EditElementInManyToManyCollectionAction extends EditElementInCollectionAction {
	
	private boolean editable;
	private boolean keyEditable;

	public void execute() throws Exception {
		super.execute();
		getCollectionElementView().setEditable(editable);
		getCollectionElementView().setKeyEditable(keyEditable);
		setControllers("ManyToManyUpdateElement", "Dialog");
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
