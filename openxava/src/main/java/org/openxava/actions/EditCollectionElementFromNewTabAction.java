package org.openxava.actions;

import java.util.*;

public class EditCollectionElementFromNewTabAction extends CollectionElementViewBaseAction implements IForwardAction {

	private String nextURI = "";
	private int row;

	@Override
	public void execute() throws Exception {
		Collection elements;
		Map keys = null;
		Map values = null;
		/*
		 		if (getCollectionElementView().isCollectionFromModel()) {
			elements = getCollectionElementView().getCollectionValues();
			if (elements == null) return;
			int rowValue = getCollectionElementView().getCollectionEditingRow();
			row = (rowValue > 0) ? rowValue : getRow();
			if (elements instanceof List) {
				if (nextValue != 0) validRowAndUpdate(row, elements.size());
				keys = (Map) ((List) elements).get(row);		
			}
		} 
		 
		 */
		if (getCollectionElementView().isCollectionFromModel()) {
			System.out.println("coll from model");
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
	public String getForwardURI() {
		//return "https://www.google.com";
		return nextURI;
	}

	private String getReferencedModel() {
		return getCollectionElementView().getModelName();
	}

	private String getReferencedId(Map map) {
		System.out.println(map);
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
