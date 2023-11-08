package org.openxava.web.dwr;

import java.rmi.*;
import java.util.*;

import javax.ejb.*;
import javax.servlet.http.*;
import javax.swing.table.*;

import org.apache.commons.lang3.*;
import org.apache.commons.logging.*;
import org.json.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.tab.Tab;
import org.openxava.util.*;
import org.openxava.view.View;
import org.openxava.view.meta.*;
import org.openxava.web.editors.*;

import lombok.*;

@Getter
@Setter
public class Tree extends DWRBase {

	private static Log log = LogFactory.getLog(Tree.class);

	public String getNodes(HttpServletRequest request, HttpServletResponse response, String application, String module,
			String collectionName) throws Exception {
		try {
			initRequest(request, response, application, module);
			String tabObject = "xava_collectionTab_" + collectionName;
			Tab tab2 = getTab(request, application, module, tabObject);
			Tab tab = tab2.clone();
			View view = getView(request, application, module);
			View collectionView = view.getSubview(collectionName);
			MetaView metaView = view.getMetaModel().getMetaView(view.getViewName());
			MetaCollectionView metaCollectionView = metaView.getMetaCollectionView(collectionName);
			org.openxava.annotations.Tree tree = metaCollectionView.getPath();
			String pathProperty = "path";
			String pathSeparator = "/";
			String idProperties = "";

			if (tree != null) {
				pathProperty = tree.pathProperty() != null ? tree.pathProperty() : "path";
				pathSeparator = tree.pathSeparator() != null ? tree.pathSeparator() : "/";
				idProperties = tree.idProperties() != null ? tree.idProperties() : "";
			}

			String[] listProperties = metaCollectionView.getPropertiesListNamesAsString().split(",");
			String order = collectionView.getMetaCollection().getOrder();
			String[] oSplit = !order.isEmpty() ? order.replaceAll("\\$\\{|\\}", "").split(", ") : new String[0];
			List<String> keysList = new ArrayList<>(tab.getMetaTab().getMetaModel().getAllKeyPropertiesNames());
			Map<String, Object> propertiesMap = new HashMap<>();

			propertiesMap.put("listProperties", listProperties);
			propertiesMap.put("path", pathProperty);
			propertiesMap.put("separator", pathSeparator);
			propertiesMap.put("id", idProperties);

			tab.clearProperties();
			for (String element : listProperties) {
				tab.addProperty(element);
			}
			if (!ArrayUtils.contains(listProperties, pathProperty))
				tab.addProperty(0, pathProperty);
			if (keysList.size() < 2 && !ArrayUtils.contains(listProperties, keysList.get(0).toString())) {
				tab.addProperty(0, keysList.get(0).toString());
			}

			JSONArray jsonArray = new JSONArray();
			TableModel table = tab.getAllDataTableModel();
			int tableSize = tab.getTableModel().getTotalSize();
			listProperties = tab.getPropertiesNamesAsString().split(",");

			if (tableSize > 0) {
				for (int i = 0; i < tableSize; i++) {
					JSONObject jsonRow = new JSONObject();
					for (int j = 0; j < listProperties.length; j++) {
						Object value = table.getValueAt(i, j);
						String propertyName = listProperties[j];
						jsonRow.put(propertyName.toLowerCase(), value);
					}
					jsonRow.put("row", i);
					jsonArray.put(jsonRow);
				}
			}

			jsonArray = TreeViewParser.findChildrenOfNode("0", jsonArray, propertiesMap, false);
			return jsonArray.toString();
		} catch (Exception e) {
			log.error(XavaResources.getString("cant_load_collection_as_tree", collectionName, module), e);
			return "";
		} finally {
			cleanRequest();
		}
	}

	public void updateNode(HttpServletRequest request, HttpServletResponse response, String application, String module,
			String collectionName, String idProperties, String pathProperty, String newPath, List<String> rows,
			List<String> childRows) throws NumberFormatException, XavaException, FinderException, RemoteException {
		try {
			initRequest(request, response, application, module);
			View view = getView(request, application, module);
			View collectionView = view.getSubview(collectionName);
			String modelName = collectionView.getMetaModel().getQualifiedName();

			Map<String, String> pathIdMap = new HashMap<>();
			pathIdMap.put(pathProperty, null);
			Map<String, String> newPathValue = new HashMap<>();
			newPathValue.put(pathProperty, newPath);

			Map<String, String> pathValueMap = new HashMap<>();
			List<String> parentsValues = new ArrayList<>();

			for (String row : rows) {
				Map keys = (Map) collectionView.getCollectionTab().getTableModel().getObjectAt(Integer.valueOf(row));
				pathValueMap = MapFacade.getValues(modelName, keys, pathIdMap);
				if (pathValueMap.get(pathProperty).equals("")) {
					if (idProperties.equals("")) {
						parentsValues.add(keys.get("id").toString());
					} else {
						// for multiple ids
					}
				} else {
					parentsValues.add(pathValueMap.get(pathProperty));
				}
				MapFacade.setValues(modelName, keys, newPathValue);
			}
			childRows.removeIf(rows::contains);

			for (String row : childRows) {
				Map keys = (Map) collectionView.getCollectionTab().getTableModel().getObjectAt(Integer.valueOf(row));
				pathValueMap = MapFacade.getValues(modelName, keys, pathIdMap);
				String childPathValue = (String) pathValueMap.get(pathProperty);
				for (String pValue : parentsValues) {
					pValue = pValue.startsWith("/") ? pValue : "/" + pValue;
					if (childPathValue.startsWith(pValue)) {
						if (!newPath.equals("")) {
							childPathValue = newPath + childPathValue.substring(childPathValue.indexOf(pValue));
						} else {
							childPathValue = childPathValue.replace(pValue, newPath);
						}
					} else if (childPathValue.contains(pValue)) {
						childPathValue = childPathValue.substring(childPathValue.indexOf(pValue) - 1);
					}
					break;
				}
				pathValueMap.clear();
				pathValueMap.put(pathProperty, childPathValue);
				MapFacade.setValues(modelName, keys, pathValueMap);
			}
		} finally {
			try {
				XPersistence.commit();
			} finally {
				cleanRequest();
			}
		}

	}

	public List<Integer> toIntegerList(List<String> stringList) {
		List<Integer> integerList = new ArrayList<>();
		for (String str : stringList) {
			int num = Integer.parseInt(str);
			integerList.add(num);
		}
		return integerList;
	}

	private static Tab getTab(HttpServletRequest request, String application, String module, String tabOject) {
		Tab tab = (Tab) getContext(request).get(application, module, tabOject);
		request.setAttribute("xava.application", application);
		request.setAttribute("xava.module", module);
		tab.setRequest(request);
		return tab;
	}

	protected View getView(HttpServletRequest request, String application, String module) {
		View view = (View) getContext(request).get(application, module, "xava_view");
		request.setAttribute("xava.application", application);
		request.setAttribute("xava.module", module);
		view.setRequest(request);
		return view;
	}

}
