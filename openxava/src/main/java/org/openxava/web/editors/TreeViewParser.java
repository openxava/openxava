package org.openxava.web.editors;

import java.util.*;
import java.util.Collections;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.json.*;
import org.openxava.annotations.*;
import org.openxava.model.meta.*;
import org.openxava.tab.Tab;
import org.openxava.util.*;
import org.openxava.view.View;
import org.openxava.view.meta.*;
import org.openxava.web.*;
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
	public String[] parse(String modelName) throws Exception {
		lastParse = new StringBuilder("");
		indexList = new StringBuilder("");
		metaTreeView = getMetaTreeView(modelName);
		if (metaTreeView != null) {
			parseGroups();
			for (String path : groups.keySet()) {
				if (!Is.empty(lastParse)) {
					lastParse.append(",");
				}
				lastParse.append(parseTreeNode(path));
			}
			
			
			lastParse.insert(0, "new YAHOO.widget.TreeView('tree_" + collectionName +
					"',[");
			lastParse.append("]);");
		}
		return new String[] {lastParse.toString(), indexList.toString()};
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
		String[] columnNames = new String[tab.getTableModel().getColumnCount()];

		// Gather columnNames
		for (int columnIndex = 0; columnIndex < tab.getTableModel().getColumnCount(); columnIndex++) {
			MetaProperty metaProperty = tab.getMetaProperty(columnIndex);
			columnNames[columnIndex] = metaProperty.getQualifiedName();
		}
		
		Map[] allKeys = tab.getAllKeys();
		
		// Initialize the reader
		reader.initialize(tab.getCollectionView().getParent().getModelName(),
				tab.getCollectionView().getParent().getKeyValues(), tab.getModelName(),  
				allKeys, columnNames);
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
		Object treeNode;
		boolean expandedState;
		String expanded="";
		StringBuilder html = new StringBuilder("");
		StringBuilder parsedChildren;
		String styleListCellSpacing = "border=\"1\" cellspacing=\"10\" cellpadding=\"10\"";
		List<TreeNodeHolder> nodesHolder;
		String tooltip = XavaResources.getString("double_click_to_edit_view");
		nodesHolder = groups.get(path);
		if (nodesHolder == null) {
			return new StringBuilder("");
		}
		if (metaTreeView.isEntityObject()) {
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
				ITreeViewReader reader = metaTreeView.getTreeViewReaderImpl();
				int index = nodeHolder.index;
				if (indexList.length() > 0) {
					indexList.append(",");
				}
				indexList.append(index);
				HttpServletRequest request = tab.getRequest();
				treeNode = nodeHolder.treeNode;
				html = new StringBuilder("");
				if (tab.getTableModel().getColumnCount() > 1) {
					html.append("<table class=\"ox-list\" ");
					html.append("width=\"100%\" ");
					html.append(styleListCellSpacing);
					html.append(" title=\"");
					html.append(tooltip);
					html.append("\"> <tr>");
					for (int c = 0; c < tab.getTableModel().getColumnCount(); c++) {
						MetaProperty p = tab.getMetaProperty(c);
						String align =p.isNumber() && !p.hasValidValues()?" ox-text-align-right":"";
						String fvalue = null;
						if (p.hasValidValues()) {
							fvalue = p.getValidValueLabel(reader.getValueAt(index, c));
						}
						else {
							fvalue = WebEditors.format(request, p, reader.getValueAt(index, c), errors, viewObject, true);
						}
						html.append("<td class=\"");
						html.append((c%2==0?"ox-list-pair":"ox-list-odd"));
						html.append(" ox-list-data-cell");
						html.append(align);
						html.append("\">");
						html.append(fvalue);
						html.append("</td"); 
					}
					html.append("</tr></table>");
				} else {
					if (tab.getTableModel().getColumnCount() == 1) {
						MetaProperty p = tab.getMetaProperty(0);
						String fvalue = null;
						if (p.hasValidValues()) {
							fvalue = p.getValidValueLabel(reader.getValueAt(index, 0));
						}
						else {
							fvalue = WebEditors.format(request, p, reader.getValueAt(index, 0), errors, viewObject, true);
						}
						html.append("&nbsp;<span title=\"");
						html.append(tooltip);
						html.append("\">");
						html.append(fvalue);
						html.append("</span>");
					}
				}
				expandedState = metaTreeView.getNodeExpandedState(treeNode);
				expanded = expandedState?",expanded:true":"";
				parsedChildren = parseTreeNode(metaTreeView.getNodeFullPath(treeNode));
				if (!Is.empty(parsedChildren)) {
					parsedChildren.insert(0, ",children:[");
					parsedChildren.append("]");
				}
				if (!Is.empty(returnValue)) {
					returnValue.append(",");
				}
				returnValue.append("{type:'html',editable:true, data:\"");
				returnValue.append(index);
				returnValue.append("\", html:'");
				returnValue.append(html);
				returnValue.append("'");
				returnValue.append(expanded);
				returnValue.append(parsedChildren);
				returnValue.append("}");
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
        String mapId = (String) map.get("id");
        String mapPath = (String) map.get("path");
        String mapSeparator = (String) map.get("separator");
        String[] mapOrder = (String[]) map.get("order");
        String[] mapTabProperties = (String[]) map.get("tabProperties");

        for (int i = 0; i < data.length(); i++) {
            JSONObject node = data.getJSONObject(i);
            String id = node.get("id").toString();
            String path = node.get(mapPath).toString();
            String order = node.get("treeorder").toString();
            String description = node.get("description").toString();
            String row = node.get("row").toString();
            // usar doble if, primero verificar rootNodeFound y luego si el path es vacio o no.. asi se puede combinar con el codigo de !rootNodeFound && json.isEmpty() 
            if (path.equals("") && parentId.equals("0")) {
                JSONObject rootNode = new JSONObject();
                rootNode.put("id", id);
                rootNode.put("path", path);
                rootNode.put("text", description);
                rootNode.put("order", order);
                rootNode.put("row", row);
                JSONArray childNodes = findChildrenOfNode(id, data, map, true);
                if (childNodes.length() > 0) {
                    rootNode.put("children", childNodes);
                }
                json.put(rootNode);
                rootNodeFound = true;
            } else if (path.endsWith("/" + parentId)) {
                JSONObject childNode = new JSONObject();
                childNode.put("id", id);
                childNode.put("text", description);
                childNode.put("path", path);
                childNode.put("order", order);
                childNode.put("row", row);
                JSONArray childNodes = findChildrenOfNode(id, data, map, rootNodeFound);
                if (childNodes.length() > 0) {
                    childNode.put("children", childNodes);
                }
                json.put(childNode);
            }

        }
    	if (!rootNodeFound && json.isEmpty()) {
    		rootNodeFound = true;
    		JSONObject node = data.getJSONObject(0);
            String id = node.get("id").toString();
            String path = node.get(mapPath).toString();
            String order = node.get("treeorder").toString();
            String description = node.get("description").toString();
            String row = node.get("row").toString();
            JSONObject rootNode = new JSONObject();
            rootNode.put("id", id);
            rootNode.put("path", path);
            rootNode.put("text", description);
            rootNode.put("order", order);
            rootNode.put("row", row);
            JSONArray childNodes = findChildrenOfNode(id, data, map, true);
            if (childNodes.length() > 0) {
            	rootNode.put("children", childNodes);
            }
        	json.put(rootNode);
    	}
        return json;
    }

}
