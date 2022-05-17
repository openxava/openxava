package org.openxava.actions;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.openxava.model.MapFacade;
import org.openxava.web.editors.*;


public class TreeViewExpandCollapseAction extends CollectionElementViewBaseAction {

	private int row;
	private boolean expanded;
	
	@SuppressWarnings("rawtypes")
	@Override
	public void execute() throws Exception {
		Collection elements = getCollectionElementView().getCollectionValues(); 
		if (elements == null) return;
		if (elements instanceof List) {
			Map keyValues = (Map)((List)elements).get(getRow());
			Object treeNode = (Object)MapFacade.findEntity(getCollectionElementView().getModelName(), keyValues);
			TreeViewParser treeViewParser = (TreeViewParser) getContext().get(getRequest(), TreeViewParser.XAVA_TREE_VIEW_PARSER);
			TreeView metaTreeView = treeViewParser.getMetaTreeView(getCollectionElementView().getModelName());
			metaTreeView.setNodeExpandedState(treeNode, expanded);
		}
	}

	/**
	 * @param row the row to set
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * @param expanded the expanded to set
	 */
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	/**
	 * @return the expanded
	 */
	public boolean isExpanded() {
		return expanded;
	}

}
