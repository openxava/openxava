package org.openxava.web.dwr;

import java.util.*;

import javax.persistence.*;
import javax.servlet.http.*;
import javax.swing.table.*;

import org.apache.commons.lang3.*;
import org.json.*;
import org.openxava.jpa.*;
import org.openxava.model.meta.*;
import org.openxava.tab.Tab;
import org.openxava.view.View;
import org.openxava.view.meta.*;
import org.openxava.web.editors.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.*;

import lombok.*;

@Getter
@Setter
public class Tree extends DWRBase {

	public String getNodes(HttpServletRequest request, HttpServletResponse response,
			String application, String module, String collectionName) throws Exception {
		String tabObject = "xava_collectionTab_" + collectionName;
		Tab tab2 = getTab(request, application, module, tabObject);
		Tab tab = tab2.clone();
		View view = getView(request, application, module);
		View collectionView = view.getSubview(collectionName);
		System.out.println(collectionView.getMetaModel().getQualifiedName());
		System.out.println(collectionName);
		View tabCollectionView2 = tab2.getCollectionView();
		System.out.println("1.2 " + tabCollectionView2.getModelName());
		View tabCollectionView = tab.getCollectionView();
		System.out.println("1.2 " + tabCollectionView.getModelName());
		JSONArray jsonArray = new JSONArray();
		String pathProperty = "path";
		String pathSeparator = "/";
		String idProperties = "";
		int orderIncrement = 2;
		// need clone table for not alterate original tab

		//tab = collectionView.getCollectionTab().clone();
		String[] propertiesNow = tab.getPropertiesNamesAsString().split(",");
		System.out.println("1"); 
		System.out.println(Arrays.toString(propertiesNow));
		MetaCollection mc = collectionView.getMetaCollection();
		String order = mc.getOrder();
		//String order = "";
		String[] oSplit = !order.isEmpty() ? order.replaceAll("\\$\\{|\\}", "").split(", ") : new String[0];
		List<String> keysList = new ArrayList<>(tab.getMetaTab().getMetaModel().getAllKeyPropertiesNames());
		System.out.println("1.1");
		Map<String, Object> propertiesMap = new HashMap<>();
		//View tabCollectionView = tab.getCollectionView();
		System.out.println("1.2 " + tabCollectionView.getModelName());
		View parentView = tabCollectionView.getParent();
		System.out.println("1.22 " + parentView.getModelName());
		MetaView metaView = parentView.getMetaModel().getMetaView(parentView.getViewName());
		System.out.println("1.3");
		MetaCollectionView metaCollectionView = metaView.getMetaCollectionView(collectionName);
		org.openxava.annotations.Tree tree = metaCollectionView.getPath();
		
		System.out.println("2");

		boolean initialState = true;
		if (tree != null) {
			pathProperty = tree.pathProperty() != null ? tree.pathProperty() : "path";
			pathSeparator = tree.pathSeparator() != null ? tree.pathSeparator() : "/";
			idProperties = tree.idProperties() != null ? tree.idProperties() : "";
			System.out.println(tree.orderIncrement());
			System.out.println(idProperties);
			System.out.println(tree.initialExpandedState());
			orderIncrement = tree.orderIncrement() != 2 ? tree.orderIncrement() : 2;
			initialState = tree.initialExpandedState();
		}

		propertiesMap.put("tabProperties", propertiesNow);
		propertiesMap.put("path", pathProperty);
		propertiesMap.put("separator", pathSeparator);
		propertiesMap.put("id", idProperties);
		propertiesMap.put("orderIncrement", orderIncrement);
		propertiesMap.put("order", oSplit);
		int count = 0;
		System.out.println("3");
		for (String element : oSplit) {
			if (ArrayUtils.contains(propertiesNow, element))
				continue;
			if (element.equals(pathProperty)) {
				tab.addProperty(0, element);
				count = count == 0 ? 0 : count++;
			} else {
				tab.addProperty(count, element);
				count++;
			}
		}
		if (keysList.size() < 2 && !ArrayUtils.contains(propertiesNow, keysList.get(0).toString())) {
			tab.addProperty(0, keysList.get(0).toString());
		}
		System.out.println("4");
		TableModel table = tab.getAllDataTableModel();
		int tableSize = tab.getTableModel().getTotalSize();
		int columns = table.getColumnCount();
		List<String> columnName = new ArrayList<>();
		ObjectMapper objectMapper = new ObjectMapper();
		List<Map<String, Object>> tableList = new ArrayList<>();
		propertiesNow = tab.getPropertiesNamesAsString().split(",");

		if (tableSize > 0) {
			for (int i = 0; i < columns; i++) {
				columnName.add(table.getColumnName(i));
			}
			for (int i = 0; i < tableSize; i++) {
				ObjectNode elementNode = JsonNodeFactory.instance.objectNode();
				JSONObject jsonRow = new JSONObject();
				for (int j = 0; j < propertiesNow.length; j++) {
					Object value = table.getValueAt(i, j);
					String propertyName = propertiesNow[j];
					jsonRow.put(propertyName.toLowerCase(), value);
				}
				jsonRow.put("row", i);
				jsonArray.put(jsonRow);
			}

		}
		System.out.println("5");
		//System.out.println(propertiesMap);
		//System.out.println(jsonArray);
		// need parse path and properties
		jsonArray = TreeViewParser.findChildrenOfNode("0", jsonArray, propertiesMap, false);
		System.out.println(jsonArray.toString());
		return jsonArray.toString();
	}

	

	public void updateNode(HttpServletRequest request, HttpServletResponse response, String application,
			String modelName, String pathProperty, List<String> nodoACambiar, String pathParent) {
		System.out.println("update");
		System.out.println(pathProperty);
		System.out.println(pathParent);
		System.out.println(nodoACambiar);
		System.out.println(application);
		List<Integer> nodosACambiar = toIntegerList(nodoACambiar);

		Query query = XPersistence.getManager()
				.createQuery("UPDATE " + modelName + " t SET t." + pathProperty + " = :newPath WHERE t.id IN (:id)");

		query.setParameter("newPath", pathParent);
		query.setParameter("id", nodosACambiar);

		query.executeUpdate();
		XPersistence.commit();
		System.out.println("termiando");

	}

	public List<Integer> toIntegerList(List<String> stringList) {
		List<Integer> integerList = new ArrayList<>();
		for (String str : stringList) {
			try {
				int num = Integer.parseInt(str);
				integerList.add(num);
			} catch (NumberFormatException e) {
				System.err.println("Error al convertir: " + str);
			}
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
	
//	public String getTreeView(HttpServletRequest request, HttpServletResponse response,
//			String application, String module) {
//		return getView(request, application, module).getMetaModel().getQualifiedName();
//	}
	

}
