package org.openxava.web.editors;

import java.util.*;

import org.apache.commons.logging.*;
import org.json.*;
import org.openxava.annotations.*;
import org.openxava.tab.Tab;
import org.openxava.util.*;
import org.openxava.view.View;
import org.openxava.view.meta.*;
import org.openxava.web.style.*;

/**
 * Parse the tree view and produces a javascript snippet.
 * 
 * @author Federico Alcantara
 */
public class TreeViewParser {
	private static Log log = LogFactory.getLog(TreeViewParser.class);
	public static final String XAVA_TREE_VIEW_PARSER = "xava_treeViewParser";
	public static final String XAVA_TREE_VIEW_NODE_FULL_PATH = "xava_treeViewNodeFullPath";
	
	private Tab tab;
	private String viewObject;
	private Style style;
	private Messages errors;
	private TreeView metaTreeView;
	private class TreeNodeHolder{
		public TreeNodeHolder(Object treeNode, int index) {
			this.treeNode = treeNode;
			this.index = index;
			this.rendered = false;
		}
		private Object treeNode;
		private int index;
		private boolean rendered;
	}
	private Map<String, List<TreeNodeHolder>> groups;
	private Tree treePath;
	
	@SuppressWarnings("rawtypes")
	private Class parentObject;
	private String collectionName;
	private Map<String, TreeView> metaTreeViews;
	private StringBuilder lastParse = null;
	private StringBuilder indexList;
	
	/**
	 * Default constructor
	 */
	public TreeViewParser(){
	}
	
	
	/**
	 * Creates the MetaTreeView for later use
	 * @param tab Tab object containing model, TableModel, etc.
	 * @param viewObject Visible object
	 * @param collectionName Name of the collection
	 * @param style Display style
	 * @param errors Errors message container
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void createMetaTreeView(Tab tab, String viewObject, String collectionName, Style style, Messages errors)
			throws Exception {
		this.tab = tab;
		this.viewObject = viewObject;
		this.style = style;
		this.collectionName = collectionName;
		Class treeNodeClass;
		metaTreeView = getMetaTreeView(tab.getModelName());
		// check if we have a previous metaTreeView
		if (metaTreeView == null) {
			// Initialize metaTreeView for further processing.
			treeNodeClass = tab.getCollectionView().getMetaModel().getPOJOClass();
			View collectionView = tab.getCollectionView();
			View parentView = collectionView.getParent();
			MetaView metaView = parentView.getMetaModel().getMetaView(parentView.getViewName());
			MetaCollectionView metaCollectionView = metaView.getMetaCollectionView(collectionName);
			// Find container
			Map keyValues = collectionView.getParent().getKeyValues();
			if (keyValues != null) {
				this.parentObject = collectionView.getParent().getMetaModel().getPOJOClass();
				treePath = null;
				if (metaCollectionView != null) {
					treePath = metaCollectionView.getPath();
				}
				metaTreeView = new TreeView(
						treePath, treeNodeClass, this.parentObject, collectionName, tab.getRequest().getParameter("reader"));
				getMetaTreeViews().put(tab.getModelName(), metaTreeView);
				log.debug("Added metaTreeView for:" + tab.getModelName());
			}
		}
	}

	/**
	 * Returns the saved metaTreeView
	 * @param modelName name of the model
	 * @return A MetaTreeView object. Should not be null.
	 */
	public TreeView getMetaTreeView(String modelName) {
		return getMetaTreeViews().get(modelName);
	}
	
	/**
	 * Creates the treeview script to be used in the jsp page
	 * @param modelName name of the model to render
	 * @return Script for creating the treeview.
	 * @throws Exception
	 */
	public String parse(String modelName) throws Exception {
		lastParse = new StringBuilder("");
		indexList = new StringBuilder("");
		metaTreeView = getMetaTreeView(modelName);
		if (metaTreeView != null) {
			parseGroups();
			for (String path : groups.keySet()) {
				parseTreeNode(path);
			}
		}
		return indexList.toString();
	}
	
	/**
	 * Creates the group dependency tree. This ensure that the tree is always 
	 * parseable, even is not well constructed.
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private void parseGroups() throws Exception {
		Object treeNode;
		String nodePath;
		groups = new TreeMap<String, List<TreeNodeHolder>>();
		List<TreeNodeHolder> nodesHolder;
		ITreeViewReader reader = metaTreeView.getTreeViewReaderImpl();
		Map[] allKeys = tab.getAllKeys();
		// Initialize the reader 
		reader.initialize(tab.getCollectionView().getParent().getModelName(),
				tab.getCollectionView().getParent().getKeyValues(), tab.getModelName(),  
				allKeys);
		int totalSize = allKeys.length;

		for (int index = 0; index < totalSize; index++) {
			treeNode = reader.getObjectAt(index);
			nodePath = metaTreeView.getNodePath(treeNode);
			nodesHolder = groups.get(nodePath);
			if (nodesHolder == null) {
				nodesHolder = new ArrayList<TreeNodeHolder>();
				groups.put(nodePath, nodesHolder);
			}
			nodesHolder.add(new TreeNodeHolder(treeNode, index));
		}
	}
	
	/**
	 * This is the parse method applicable to each node.
	 * @param path Path to be parsed
	 * @return A stringbuilder with the script for the node and its children
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private StringBuilder parseTreeNode(String path) throws Exception {
		StringBuilder returnValue = new StringBuilder("");
		List<TreeNodeHolder> nodesHolder;
		nodesHolder = groups.get(path);
		if (nodesHolder == null) {
			return new StringBuilder("");
		}
		if (metaTreeView.isEntityObject()) {
			//when use order
			Collections.sort(nodesHolder, new Comparator(){
				public int compare(Object object1, Object object2) {
					Integer ord1 = metaTreeView.getNodeOrder(((TreeNodeHolder)object1).treeNode);
					Integer ord2 = metaTreeView.getNodeOrder(((TreeNodeHolder)object2).treeNode);
					return ord1.compareTo(ord2);
				}});
		}
		for (TreeNodeHolder nodeHolder : nodesHolder) {
			if (!nodeHolder.rendered) {
				nodeHolder.rendered = true;
				int index = nodeHolder.index;
				if (indexList.length() > 0) {
					indexList.append(",");
				}
				indexList.append(index);
			} else {
				break; // if the first one has been rendered, the rest had been too.
			}
		}
		return returnValue;
	}


	/**
	 * Returns the metaTreeviews Map that stores the already
	 * processed Metatreeview.
	 * @return A map, never null
	 */
	public Map<String, TreeView> getMetaTreeViews() {
		if (metaTreeViews == null) {
			metaTreeViews = new HashMap<String, TreeView>();
		}
		return metaTreeViews;
	}
	
	public static JSONArray findChildrenOfNode(String parentId, JSONArray data, Map<String, Object> map, boolean rootNodeFound) {
        if (data.isEmpty()) return new JSONArray();
        
		JSONArray json = new JSONArray();
        String mapId = map.get("id").toString();
        String[] listProperties = (String[]) map.get("listProperties");
        JSONObject tooltip = new JSONObject();
        tooltip.put("title", XavaResources.getString("double_click_to_edit_view"));
        
        preprocessDescriptions(data, listProperties);
		List<JSONObject> rootNodes = findRootNodes(data);
        
	    for (JSONObject rootNode : rootNodes) {
	        String rootId = rootNode.get(mapId).toString();
	        String rootPath = rootNode.get("path").toString();
	        rootNode.put("a_attr", tooltip);

	        JSONArray childNodes = findChildren(rootId, rootPath, data, map);
	        if (childNodes.length() > 0) {
	            rootNode.put("children", childNodes);
	        }
	        json.put(rootNode);
	    }

        return json;
    }
	
	
	private static List<JSONObject> findRootNodes(JSONArray data) {
	    List<JSONObject> rootNodes = new ArrayList<>();
	    for (int i = 0; i < data.length(); i++) {
	        JSONObject node = data.getJSONObject(i);
	        String path = node.get("path").toString();
	        if (path.equals("")) {
	            rootNodes.add(node);
	        }
	    }
	    if (rootNodes.isEmpty()) {
	        List<JSONObject> candidates = new ArrayList<>();
	        String minPath = null;
	        for (int i = 0; i < data.length(); i++) {
	            JSONObject node = data.getJSONObject(i);
	            String path = node.get("path").toString();
	            if (!path.isEmpty()) {
	                if (minPath == null || path.compareTo(minPath) < 0) {
	                    candidates.clear();
	                    candidates.add(node);
	                    minPath = path;
	                } else if (path.equals(minPath)) {
	                    candidates.add(node);
	                }
	            }
	        }
	        if (!candidates.isEmpty()) {
	            rootNodes.addAll(candidates);
	        }
	    }
	    return rootNodes;
	}

	
	private static JSONArray findChildren(String parentId, String parentPath, JSONArray data, Map<String, Object> map) {
	    JSONArray children = new JSONArray();
	    String mapId = map.get("id").toString();
        String separator = (String) map.get("pathSeparator");

	    for (int i = 0; i < data.length(); i++) {
	        JSONObject node = data.getJSONObject(i);
	        String path = node.get("path").toString();
	        String id = node.get(mapId).toString();
	        if (path.equals(parentPath + separator + parentId)) {
	            node.put("a_attr", new JSONObject().put("title", XavaResources.getString("double_click_to_edit_view")));
	            JSONArray childNodes = findChildren(id, path, data, map);
	            if (childNodes.length() > 0) {
	                node.put("children", childNodes);
	            }
	            children.put(node);
	        }
	    }

	    return children;
	}
	
	private static void preprocessDescriptions(JSONArray data, String[] listProperties) {
	    for (int i = 0; i < data.length(); i++) {
	        JSONObject node = data.getJSONObject(i);
	        if (!node.has("text")) {
	            buildDescriptionIfAbsent(node, listProperties);
	        }
	    }
	}
	
	private static String buildDescriptionIfAbsent(JSONObject node, String[] listProperties) {
	    if (!node.has("text")) {
	        StringBuilder description = new StringBuilder();
	        int c = 0;
	        for (String p : listProperties) {
	            String value = node.optString(p.toLowerCase().trim());
	            description.append(value);
	            if (c < listProperties.length - 1) {
	                description.append(", ");
	            }
	            c++;
	        }
	        node.put("text", description.toString());
	    }
	    return node.getString("text");
	}
}
