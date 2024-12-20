package org.openxava.actions;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.model.*;
import org.openxava.web.editors.*;



public class SaveElementInTreeViewAction extends SaveElementInCollectionAction {
	public static Log log = LogFactory.getLog(SaveElementInCollectionAction.class);
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Map getValuesToSave() throws Exception {
		Map returnValue = super.getValuesToSave();
		Object entity = null;
		try {
			entity = MapFacade.findEntity(getCollectionElementView().getModelName(), getCollectionElementView().getKeyValues());
		} catch (Exception e) {
			log.debug(e.getMessage());
		}
		// This should only be done on new elements!
		if (entity == null) {
			TreeViewParser treeViewParser = (TreeViewParser) getContext().get(getRequest(), TreeViewParser.XAVA_TREE_VIEW_PARSER);
			TreeView metaTreeView = treeViewParser.getMetaTreeView(getCollectionElementView().getModelName());
			if (metaTreeView != null && returnValue != null){
				if (returnValue.containsKey(metaTreeView.getIdProperties())) {
					Map searchingValues = new HashMap<>();
					Map memberNames = new HashMap<>();
					searchingValues.put(metaTreeView.getIdProperties(), returnValue.get(metaTreeView.getIdProperties()));
					memberNames.put(metaTreeView.getIdProperties(), null);
					Map map = null;
					map = MapFacade.getValuesByAnyProperty(getCollectionElementView().getModelName(), searchingValues, memberNames);
					if (map != null) {
						addError("tree_duplicated_id", 
								returnValue.get(metaTreeView.getIdProperties()),
								metaTreeView.getIdProperties(),
								getCollectionElementView().getModelName());
						return new HashMap<>();
					}
				}
				if (!returnValue.containsKey(metaTreeView.getPathProperty())) {
					String fullPath = (String) getContext().get(getRequest(), TreeViewParser.XAVA_TREE_VIEW_NODE_FULL_PATH);
					if (fullPath != null) {
						returnValue.put(metaTreeView.getPathProperty(), fullPath);
					}
				}
				if (metaTreeView.isOrderDefined() &&
						!returnValue.containsKey(metaTreeView.getOrderProperty())) {
					Integer newOrder = 0;
					if (getCollectionElementView().isCollectionFromModel()) {
						newOrder = getCollectionElementView().getCollectionValues().size();
					} else {
						newOrder = getCollectionElementView().getCollectionTab().getTotalSize();
					}
					newOrder = newOrder * metaTreeView.getKeyIncrement();
					returnValue.put(metaTreeView.getOrderProperty(), newOrder);
				}
			}
		}
		
		getCollectionElementView().collectionDeselectAll();

		return returnValue;
	}
}
