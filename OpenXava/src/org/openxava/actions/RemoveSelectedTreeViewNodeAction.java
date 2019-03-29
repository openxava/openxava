package org.openxava.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.model.MapFacade;
import org.openxava.validators.ValidationException;
import org.openxava.web.editors.*;

public class RemoveSelectedTreeViewNodeAction extends CollectionBaseAction {
	private Log log = LogFactory.getLog(RemoveSelectedTreeViewNodeAction.class);
	
	@SuppressWarnings("rawtypes")
	public void execute() throws Exception {
		TreeViewParser treeViewParser = (TreeViewParser) getContext().get(getRequest(), TreeViewParser.XAVA_TREE_VIEW_PARSER);
		TreeView metaTreeView = treeViewParser.getMetaTreeView(getCollectionElementView().getModelName());
		try{						
			Map [] selectedOnes = getSelectedKeys(); 
			List<Map> selected = new ArrayList<Map>();
			if (selectedOnes.length > 0){
				for (Map values: selectedOnes) {
					selected.add(values);
					Object treeNode = MapFacade.findEntity(getCollectionElementView().getModelName(), values);
					if (metaTreeView != null){
						try {
							String prefix = metaTreeView.getNodeFullPath(treeNode);
							for (Object entity : getCollectionElementView().getCollectionObjects()) {
								if (metaTreeView.getNodePath(entity).startsWith(prefix)) {
									values = MapFacade.getKeyValues(getCollectionElementView().getModelName(), entity);
									selected.add(values);
								}
							}
						} catch (Exception e) {
							log.debug(e.getMessage());
						}
					}
				}
				
				for (Map values: selected) {
					removeElement(values);
				}
				
				if (isEntityReferencesCollection()) {
					addMessage("association_removed", getCollectionElementView().getModelName(), 
							getCollectionElementView().getParent().getModelName());
				}
				else {
					addMessage("aggregate_removed", getCollectionElementView().getModelName());
				}
				getView().recalculateProperties();
				getCollectionElementView().collectionDeselectAll();
			}
		}
		catch (ValidationException ex) {			
			addErrors(ex.getErrors());
		}
	}
	
	/**
	 * Is called for each selected row with the values that includes the key
	 * values. <p>
	 */
	@SuppressWarnings("rawtypes")
	protected void removeElement(Map values) throws Exception {
		MapFacade.removeCollectionElement(getCollectionElementView().getParent().getModelName(), getCollectionElementView().getParent().getKeyValues(), getCollectionElementView().getMemberName(), values);
	}


}
