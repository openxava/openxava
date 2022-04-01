package org.openxava.actions;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.model.*;
import org.openxava.web.editors.*;

/**
 * 
 * @author Federico Alcantara 
 */

public class NewTreeViewItemAction extends CollectionElementViewBaseAction {
	private static Log log = LogFactory.getLog(NewTreeViewItemAction.class);
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void execute() throws Exception {
		String fullPath = null;
		List<Map> keyValues = getCollectionElementView().getCollectionSelectedValues(); 
		if (getCollectionElementView().isRepresentsAggregate()) {
			getCollectionElementView().reset();				
		}
		
		// if we have a selected one let's add one as a child
		if (keyValues.size() > 0) {	
			Map keyValue = (Map)keyValues.get(keyValues.size() - 1);
			
			Object treeNode = MapFacade.findEntity(getCollectionElementView().getModelName(), keyValue);
			TreeViewParser treeViewParser = (TreeViewParser) getContext().get(getRequest(), TreeViewParser.XAVA_TREE_VIEW_PARSER);
			TreeView metaTreeView = treeViewParser.getMetaTreeView(getCollectionElementView().getModelName());
			if (metaTreeView != null){
				try {
					fullPath = metaTreeView.getNodeFullPath(treeNode);
				} catch (Exception ex) {
					log.debug(ex);
				}
			}
			
		}
		
		getContext().put(getRequest(), TreeViewParser.XAVA_TREE_VIEW_NODE_FULL_PATH, fullPath);
		getCollectionElementView().setCollectionDetailVisible(true);
		getCollectionElementView().setCollectionEditingRow(-1);
		showDialog(getCollectionElementView());		
		if (getCollectionElementView().isCollectionEditable() || 
			getCollectionElementView().isCollectionMembersEditables()) 
		{ 
			addActions(getCollectionElementView().getSaveCollectionElementAction());
		} 		
		Iterator itDetailActions = getCollectionElementView().getActionsNamesDetail().iterator();		
		while (itDetailActions.hasNext()) {			
			addActions(itDetailActions.next().toString());			
		}
		addActions(getCollectionElementView().getHideCollectionElementAction());
	}

}
