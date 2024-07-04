package org.openxava.actions;

import java.util.*;

public class EditCollectionElementFromNewTabAction extends CollectionElementViewBaseAction implements IForwardAction, IAvailableAction {

	private String nextURI = "";
	private int row;

	@Override
	public void execute() throws Exception {
		Collection elements;
		Map keys = null;
		if (getCollectionElementView().isCollectionFromModel()) {
			elements = getCollectionElementView().getCollectionValues();
			if (elements == null) return;
			int rowValue = getCollectionElementView().getCollectionEditingRow();
			row = (rowValue > 0) ? rowValue : getRow();
			if (elements instanceof List) {
				keys = (Map) ((List) elements).get(row);
			}
		} else {
			keys = (Map) getCollectionElementView().getCollectionTab().getTableModel().getObjectAt(row);
		}
		if (keys != null) {
			nextURI = "/m/" + getReferencedModel() + "?detail=" + getReferencedId(keys);
		}

	}
	
	@Override
	public boolean isAvailable() {
		return !(getCollectionElementView().getMetaModel().getAllKeyPropertiesNames().size() > 1);
	}

	@Override
	public String getForwardURI() {
		return nextURI;
	}

	private String getReferencedModel() {
		return getCollectionElementView().getMetaModel().getName();
	}

	private String getReferencedId(Map map) {
		String firstValue = map.values().iterator().next().toString();
		return firstValue;
	}

	@Override
	public boolean inNewWindow() {
		return true;
	}

	public int getRow() {
		return row;
	}

	public void setRow(int i) {
		row = i;
	}

}
