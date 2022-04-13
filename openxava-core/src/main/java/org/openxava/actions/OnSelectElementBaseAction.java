package org.openxava.actions;

/**
 * Base action for actions executed when a collection element is selected or unselected. <p>  
 * 
 * This type of action is defined using {link org.openxava.annoations.OnSelectElementAction} 
 * annotation, or its XML counterpart <code>on-select-element-action</code>.<br>
 * This is a convenience class, if you want you can extend directly from
 * {link CollectionBaseAction} and add the properties <code>row</code> and <code>selected</code>
 * to your own action.<br>  
 * 
 * @author Javier Paniza
 */

abstract public class OnSelectElementBaseAction extends CollectionBaseAction {

	private int row;	
	private boolean selected;
	
	abstract public void execute() throws Exception;
	
	protected boolean mustRefreshCollection() { 
		return false;
	}

	/**
	 * Row number of the selected or unselected element.
	 */
	public int getRow() {
		return row;
	}

	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * If the element has been selected or unselected.
	 */
	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

}
