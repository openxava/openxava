package org.openxava.actions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.openxava.model.MapFacade;
import org.openxava.validators.ValidationException;
import org.openxava.web.editors.*;

public class RemoveTreeViewNodeAction extends CollectionElementViewBaseAction {

	@SuppressWarnings("rawtypes")
	public void execute() throws Exception {
		String path;
		List<Map> selectedOnes = new ArrayList<Map>();
		try {											
			if (!getCollectionElementView().getKeyValuesWithValue().isEmpty()) {				
				Map keyValues = getCollectionElementView().getKeyValues();
				Object treeNode = MapFacade.findEntity(getCollectionElementView().getModelName(), keyValues);
				TreeViewParser treeViewParser = (TreeViewParser) getContext().get(getRequest(), TreeViewParser.XAVA_TREE_VIEW_PARSER);
				TreeView metaTreeView = treeViewParser.getMetaTreeView(getCollectionElementView().getModelName());
				path = metaTreeView.getNodeFullPath(treeNode);
				selectedOnes.add(keyValues);
				if (metaTreeView != null) {
					for (Object entity : getCollectionElementView().getCollectionObjects()) {
						if (metaTreeView.getNodePath(entity).startsWith(path)) {
							keyValues = MapFacade.getKeyValues(getCollectionElementView().getModelName(), entity);
							selectedOnes.add(keyValues);
						}
					}
				}
				Iterator it = selectedOnes.iterator();
				while (it.hasNext()) {
					Map values = (Map)it.next();
					MapFacade.removeCollectionElement(getCollectionElementView().getParent().getModelName(), getCollectionElementView().getParent().getKeyValues(), getCollectionElementView().getMemberName(), values);
				}
				if (isEntityReferencesCollection()) {
					addMessage("association_removed", getCollectionElementView().getModelName(), getCollectionElementView().getParent().getModelName());
				}
				else {
					addMessage("aggregate_removed", getCollectionElementView().getModelName());
				}
			}
					
			getCollectionElementView().setCollectionEditingRow(-1);
			getCollectionElementView().clear();
			getView().recalculateProperties();
			closeDialog(); 
		}
		catch (ValidationException ex) {			
			addErrors(ex.getErrors());
		}				
	}
		

}
