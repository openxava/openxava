package org.openxava.web.dwr;

import java.lang.annotation.*;
import java.util.*;

import javax.servlet.http.*;
import javax.swing.table.*;

import org.apache.commons.lang3.*;
import org.apache.commons.logging.*;
import org.json.*;
import org.openxava.jpa.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
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
			String collectionName, String collectionViewParentName) throws Exception {
		try {
			initRequest(request, response, application, module);
			String tabObject = "xava_collectionTab_" + collectionName;
			Tab tab2 = getTab(request, application, module, tabObject);
			Tab tab = tab2.clone();
			View view = getView(request, application, module); // root
			MetaView metaView = view.getMetaModel().getMetaView(view.getViewName());
			MetaCollectionView metaCollectionView = collectionViewParentName.isEmpty() 
					? metaView.getMetaCollectionView(collectionName)
					: view.getSubview(collectionViewParentName).getMetaView().getMetaCollectionView(collectionName);
			org.openxava.annotations.Tree tree = metaCollectionView.getPath();
			String pathProperty = tree != null && tree.pathProperty() !=null ? tree.pathProperty() : "path";
			String pathSeparator = tree != null && tree.pathSeparator() !=null ? tree.pathSeparator() : "/";
			String idProperties = tree != null && tree.idProperties() !=null ? tree.idProperties() : "";
			
			
			String[] listProperties = metaCollectionView.getPropertiesListNamesAsString().split(",");
			List<String> keysList = new ArrayList<>(tab.getMetaTab().getMetaModel().getAllKeyPropertiesNames());
			Map<String, Object> propertiesMap = new HashMap<>();
			propertiesMap.put("listProperties", listProperties);
			propertiesMap.put("pathSeparator", pathSeparator);
			propertiesMap.put("id", idProperties.isEmpty() ? String.join(",", keysList) : idProperties); //multiple ids not supported

			tab.clearProperties(); 
 
			for (String element : listProperties) {
				tab.addProperty(element.trim());
			}
			if (!ArrayUtils.contains(listProperties, pathProperty)) tab.addProperty(0, pathProperty);
			//need separator
			if (!idProperties.isEmpty()) tab.addProperty(0, idProperties);
			if (idProperties.isEmpty() && !ArrayUtils.contains(listProperties, keysList.get(0).toString())) {
				tab.addProperty(0, keysList.get(0).toString());
			}
			
			JSONArray jsonArray = new JSONArray();
			TableModel table = tab.getAllDataTableModel();
			int tableSize = tab.getTableModel().getTotalSize();
			listProperties = tab.getPropertiesNamesAsString().split(",");
			Set<String> processedIds = new HashSet<>();
			List<String> duplicatedIds = new ArrayList<>();
			if (tableSize > 0) {
				for (int i = 0; i < tableSize; i++) {
					JSONObject jsonRow = new JSONObject();
					for (int j = 0; j < listProperties.length; j++) {
						Object value = table.getValueAt(i, j);
						String propertyName = listProperties[j];
						if (propertyName.equals(pathProperty)) {
						    propertyName = "path";
						} else if (propertyName.equals(idProperties)) {
						    propertyName = "id";
					        if (!processedIds.add(value.toString())) {
					        	duplicatedIds.add(value.toString());
					        }
						}		
						jsonRow.put(propertyName.toLowerCase(), value);
					}
					jsonRow.put("row", i);
					jsonArray.put(jsonRow);
				}
			}
			
			propertiesMap.put("id", "id");//force id in order to work with idProperties

			if (!duplicatedIds.isEmpty()) {
				log.warn(XavaResources.getString("tree_duplicated_ids", 
						duplicatedIds,
						idProperties,
						collectionName));
				throw new XavaException("tree_duplicated_ids", 
						duplicatedIds,
	                    idProperties,
	                    collectionName);
			}
			
			jsonArray = TreeViewParser.findChildrenOfNode("0", jsonArray, propertiesMap, false);
			return jsonArray.toString();
		} catch (Exception e) {
			log.error(XavaResources.getString("cant_load_collection_as_tree", collectionName, module), e);
			throw e;
		} finally {
			XPersistence.commit();
			cleanRequest();
		}
	}

	public void updateNode(HttpServletRequest request, HttpServletResponse response, String application, String module,
			String collectionName, String collectionViewParentName, String newPath, List<String> rows,
			List<String> childRows, List<String> newOrder) throws Exception {
		try {
			initRequest(request, response, application, module);
			View view = getView(request, application, module);
			View collectionView = view.getSubview(collectionName);
			String modelName = collectionView.getMetaModel().getQualifiedName();
			MetaView metaView = view.getMetaModel().getMetaView(view.getViewName());
			MetaCollectionView metaCollectionView = collectionViewParentName.isEmpty() 
					? metaView.getMetaCollectionView(collectionName)
					: view.getSubview(collectionViewParentName).getMetaView().getMetaCollectionView(collectionName);
			org.openxava.annotations.Tree tree = metaCollectionView.getPath();
			String pathProperty = tree != null && tree.pathProperty() !=null ? tree.pathProperty() : "path";
			String pathSeparator = tree != null && tree.pathSeparator() !=null ? tree.pathSeparator() : "/";
			String idProperties = tree != null && tree.idProperties() !=null ? tree.idProperties() : "";
			String orderProperty = "";
			Map<String, Object> newOrderMap = new HashMap<>();
			newPath = newPath.replace("/", pathSeparator);
			MetaCollection metaCollection = view.getMetaModel().getMetaCollection(collectionName);
			MetaModel metaModel = collectionView.getMetaModel();

			for (Annotation annotation : metaCollection.getAnnotations()) {
			    if (annotation instanceof javax.persistence.OrderBy) {
			    	String[] values = ((javax.persistence.OrderBy) annotation).value().split(",");
			    	for (int i = 0; i < values.length; i++) {
			    	    String v = values[i].trim();
			    	    MetaProperty mp = metaModel.getMetaProperty(v);
			    	    if (mp.isInteger()) {
			    	    	orderProperty = mp.getName();
			    	    	for (String item : newOrder) {
			    	            String[] parts = item.split(":");
			    	            String row = parts[0];
			    	            Object order = mp.parse(parts[1]);
			    	            newOrderMap.put(row, order);
			    	        }
			    	    	break;
			    	    }
			    	}
			    	break;
			    }
			}

			Map<String, String> pathIdMap = new HashMap<>();
			pathIdMap.put(pathProperty, null);
			Map<String, Object> newNodeValue = new HashMap<>();
			newNodeValue.put(pathProperty, newPath);

			Map<String, String> pathValueMap = new HashMap<>();
			Set<String> parentsValues = new HashSet<>();

			for (String row : newOrderMap.keySet()) {
			    Map keys = (Map) collectionView.getCollectionTab().getTableModel().getObjectAt(Integer.valueOf(row));
			    if (rows.contains(row)) {
			        pathValueMap = MapFacade.getValues(modelName, keys, pathIdMap);
			        newNodeValue.put(orderProperty, newOrderMap.get(row));
			        parentsValues.add(pathValueMap.get(pathProperty));
			        MapFacade.setValues(modelName, keys, newNodeValue);
			    } else {
				    Map newOrderValues = new HashMap<>();
				    newOrderValues.put(orderProperty, newOrderMap.get(row));
				    MapFacade.setValues(modelName, keys, newOrderValues);
			    }
			}
			
			childRows.removeIf(rows::contains);

			for (String row : childRows) {
				Map keys = (Map) collectionView.getCollectionTab().getTableModel().getObjectAt(Integer.valueOf(row));
				pathValueMap = MapFacade.getValues(modelName, keys, pathIdMap);
				String childPathValue = (String) pathValueMap.get(pathProperty);
				for (String parentPathValue : parentsValues) {
					if (parentPathValue.isEmpty()) {
						childPathValue = newPath + childPathValue;
						break;
					}
					parentPathValue = parentPathValue.startsWith(pathSeparator) ? parentPathValue : pathSeparator + parentPathValue; 
					if (childPathValue.startsWith(parentPathValue)) {
						if (newPath.equals("")) {
							childPathValue = childPathValue.replace(parentPathValue, newPath);
						} else {
							if (parentPathValue.startsWith(newPath)) {
								childPathValue = childPathValue.replace(parentPathValue, newPath);
							} else {
								childPathValue = newPath + childPathValue.substring(childPathValue.indexOf(parentPathValue)  + parentPathValue.length());
							}
						}
					} else if (childPathValue.contains(parentPathValue)) {
						//for special cases
						childPathValue = childPathValue.substring(childPathValue.indexOf(parentPathValue) - 1);
					}
					break;
				}
				pathValueMap.clear();
				pathValueMap.put(pathProperty, childPathValue);
				MapFacade.setValues(modelName, keys, pathValueMap);
			}
			XPersistence.commit();
		} catch (Exception e) {
			log.error(XavaResources.getString("error_with_node_value", collectionName, module), e);
			XPersistence.rollback();
			throw e;
		} finally {
			cleanRequest();
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
