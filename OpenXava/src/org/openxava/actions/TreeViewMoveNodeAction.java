package org.openxava.actions;

import java.util.*;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.web.editors.*;

public class TreeViewMoveNodeAction extends CollectionElementViewBaseAction {
	private static Log log = LogFactory.getLog(TreeViewMoveNodeAction.class);
	private final String UP = "UP";
	private final String DOWN = "DOWN";
	private final String LEFT = "LEFT";
	private final String RIGHT = "RIGHT";
	
	private String actionType = LEFT;
	private Boolean clearAfter = true;
	private int row = -1;
	private List<Object> entities;
	private List<Object> selectedEntities;
	private TreeView metaTreeView;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execute() throws Exception {
		Object node = null;
		
		Map[] selected = row != -1 ? 
			new Map[] {(Map)getCollectionElementView().getCollectionTab().getTableModel().getObjectAt(row)} :
			getCollectionElementView().getCollectionTab().getSelectedKeys();
		
		entities = getCollectionElementView().getCollectionObjects();
		selectedEntities = new ArrayList<Object>();
		
		if (entities.size() > 0) {
			for (Map selectedRow: selected) {
				Object o = MapFacade.findEntity(getCollectionElementView().getModelName(), selectedRow);
				selectedEntities.add(o);
			}
			node = entities.get(0);
			TreeViewParser treeViewParser = (TreeViewParser) getContext().get(getRequest(), TreeViewParser.XAVA_TREE_VIEW_PARSER);
			metaTreeView = treeViewParser.getMetaTreeView(getCollectionElementView().getModelName());
			if (metaTreeView !=null) {
				Collections.sort(entities, new Comparator() {
					public int compare(Object object1, Object object2) {
						Integer ord1 = metaTreeView.getNodeOrder(object1);
						Integer ord2 = metaTreeView.getNodeOrder(object2);
						return ord1.compareTo(ord2);
					}});
			
				for (Object treeNode : selectedEntities) {
					if (getActionType().equals(UP)) {
						levelUp(treeNode);
					}
					if (getActionType().equals(DOWN)) {
						levelDown(treeNode);
					}
					if (getActionType().equals(LEFT)) {
						levelLeft(treeNode);
					}
					if (getActionType().equals(RIGHT)) {
						levelRight(treeNode);
					}
				}
				saveLevels();
			}
		}
	}
	

	private void levelUp(Object treeNode) {
		int newOrder = metaTreeView.getKeyIncrement() / 2;
		if (metaTreeView.isOrderDefined()) {
			try {
				String path = metaTreeView.getNodePath(treeNode);
				String fullPath = metaTreeView.getNodeFullPath(treeNode);
				sort(path);
				for (Object entity : entities) {
					if (path.equals(metaTreeView.getNodePath(entity))) {
						if (fullPath.equals(metaTreeView.getNodeFullPath(entity))){
							metaTreeView.setNodeOrder(treeNode, newOrder);
							break;
						}
						newOrder = metaTreeView.getNodeOrder(entity) - (metaTreeView.getKeyIncrement() / 2);
					}
				}
				sort(path);
				
			} catch (Exception e) {
				log.error(e);
			}
		}
	}
	
	private void levelDown(Object treeNode) {
		int newOrder = Integer.MAX_VALUE;
		if (metaTreeView.isOrderDefined()) {
			try {
				String path = metaTreeView.getNodePath(treeNode);
				String fullPath = metaTreeView.getNodeFullPath(treeNode);
				sort(path);
				for (int index = entities.size() - 1; index >= 0 ; index--) {
					Object entity = entities.get(index);
					if (path.equals(metaTreeView.getNodePath(entity))) {
						if (fullPath.equals(metaTreeView.getNodeFullPath(entity))){
							metaTreeView.setNodeOrder(treeNode, newOrder);
							break;
						}
						newOrder = metaTreeView.getNodeOrder(entity) + (metaTreeView.getKeyIncrement() / 2);
					}
				}
				sort(path);
				
			} catch (Exception e) {
				log.error(e);
			}
		}
	}
	
	private void levelLeft(Object treeNode) throws Exception {
		String path = metaTreeView.getNodePath(treeNode);
		String fullPath = metaTreeView.getNodeFullPath(treeNode);
		sort(path);
		List<Object> children = findStartWithPath(fullPath);
		if (metaTreeView.isOrderDefined()) {
			Object parentObject = findParent(treeNode);
			if (parentObject != null) {
				sort(metaTreeView.getNodePath(parentObject));
				metaTreeView.setNodeOrder(treeNode, metaTreeView.getNodeOrder(parentObject) + 
						metaTreeView.getKeyIncrement() / 2);
			}
		}

		if (path.contains(metaTreeView.getPathSeparator())) {
			path = path.substring(0, path.lastIndexOf(metaTreeView.getPathSeparator()));
		} else {
			path = "";
		}
		metaTreeView.setNodePath(treeNode, path);
		replaceStartPath(children, fullPath, metaTreeView.getNodeFullPath(treeNode));
		sort(path);
	}
	
	private void levelRight(Object treeNode) throws Exception {
		String path = metaTreeView.getNodePath(treeNode);
		String newPath = path;
		sort(path);
		String fullPath = metaTreeView.getNodeFullPath(treeNode);
		List<Object> children = findStartWithPath(fullPath);
		try {
			for (Object entity : entities) {
				if (path.equals(metaTreeView.getNodePath(entity))) {
					if (fullPath.equals(metaTreeView.getNodeFullPath(entity))){
						if (!newPath.equals(path)) {
							metaTreeView.setNodePath(treeNode, newPath);
							if (metaTreeView.isOrderDefined()) {
								metaTreeView.setNodeOrder(treeNode, Integer.MAX_VALUE);
							}
						}
						break;
					}
					newPath = metaTreeView.getNodeFullPath(entity);
				}
			}
			replaceStartPath(children, fullPath, metaTreeView.getNodeFullPath(treeNode));
			sort(path);
			if (!newPath.equals(path)) {
				sort(newPath);
			}
			
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	private List<Object> findStartWithPath(String path) throws Exception {
		List<Object> returnValue = new ArrayList<Object>();
		for (Object entity : entities) {
			if (metaTreeView.getNodePath(entity).startsWith(path)) {
				returnValue.add(entity);
			}
		}
		return returnValue;
	}
	
	private void replaceStartPath(List<Object> treeNodes, String path, String newPath) throws Exception {
		for (Object treeNode : treeNodes) {
			String objectPath = metaTreeView.getNodePath(treeNode);
			String objectNewPath = objectPath.replaceFirst(path, newPath);
			metaTreeView.setNodePath(treeNode, objectNewPath);
		}
	}
	
	private Object findParent(Object object) throws Exception {
		String path = metaTreeView.getNodePath(object);
		Object returnValue = null;
		if (!Is.empty(path)) {
			for (Object entity : entities) {
				if (path.equals(metaTreeView.getNodeFullPath(entity))) {
					returnValue = entity;
					break;
				}
			}
		}
		return returnValue;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void sort(String path) throws Exception {
		if (metaTreeView.isOrderDefined()) {
			List<Object> preList = new ArrayList<Object>();
			for (Object entity : entities) {
				if (path.equals(metaTreeView.getNodePath(entity))) {
					preList.add(entity);
				}
			}
			Collections.sort(preList, new Comparator() {
				public int compare(Object object0, Object object1) {
					Integer ord0 = metaTreeView.getNodeOrder(object0);
					Integer ord1 = metaTreeView.getNodeOrder(object1); 
					return ord0.compareTo(ord1);
				}});
			int newOrder = metaTreeView.getKeyIncrement();
			for (Object entity : preList) {
				metaTreeView.setNodeOrder(entity, newOrder);
				newOrder += metaTreeView.getKeyIncrement();
			}
		}
	}

	private void saveLevels() {
		if (metaTreeView.isEntityObject()) {
			for (Object entity:entities) {
				XPersistence.getManager().merge(entity);
			}
		}
	}
		
	/**
	 * @return the actionType
	 */
	public String getActionType() {
		return actionType;
	}

	/**
	 * @param actionType the actionType to set
	 */
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	/**
	 * @return the clearAfter
	 */
	public Boolean getClearAfter() {
		return clearAfter;
	}

	/**
	 * @param clearAfter the clearAfter to set
	 */
	public void setClearAfter(Boolean clearAfter) {
		this.clearAfter = clearAfter;
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
	
}
