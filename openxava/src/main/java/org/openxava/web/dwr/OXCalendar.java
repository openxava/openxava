package org.openxava.web.dwr;

import java.math.*;
import java.rmi.*;
import java.util.*;

import javax.servlet.http.*;
import javax.swing.table.*;

import org.json.*;
import org.openxava.filters.*;
import org.openxava.formatters.*;
import org.openxava.model.meta.*;
import org.openxava.tab.Tab;
import org.openxava.util.*;
import org.openxava.view.View;
import org.openxava.web.*;
import org.openxava.web.editors.*;

import lombok.*;

@Getter
@Setter
public class OXCalendar extends DWRBase {

	transient private HttpServletRequest request;
	transient private HttpServletResponse response;
	private String application;
	private String module;
	// private List<CalendarEvent> calendarEvents;
	private Tab tab;
	private Tab tab2;
	private DateFilter filter;
	private TableModel table;
	private String date2;
	int titleFirstIdx = -1;
	private BooleanFormatter booleanFormatter;
	//private static Messages errors;
	//private static View view;
	private Messages errors;
	private View view;
	private CalendarEvent event;
	private CalendarEventIterator ci;
	private List<String> keysList;
	
	int keysListSize = 0;
	int datesListSize = 0;
	int properties1ListSize = 0;
	int properties2ListSize = 0;

	// List<CalendarEvent>
	public String getEvents(HttpServletRequest request, HttpServletResponse response, String application, String module,
			String month) throws RemoteException {
		System.out.println("dwr");
		this.application = application;
		this.module = module;
		this.response = response;
		this.view = getView(request, application, module);
		errors = new Messages();

		List<CalendarEvent> calendarEvents = new ArrayList<>();

		String tabObject = "xava_tab";
		tab2 = getTab(request, application, module, tabObject);
		tab = tab2.clone();
		// obtener el filtro del mes actual o del mes recibido
		DateFilter filter = new DateFilter();
		filter = setFilterForMonth(month);
		tab.setFilter(filter);
		tab.setBaseCondition("date between ? and ?");
		tab = setProperties(tab);

		this.table = tab.getTableModel();

		// obtener todos los eventos del tab clonado
		// como ya se como es la tabla ahora, uso los size para determinar que columna
		// agregar

		int tableSize = 0;
		tableSize = tab.getTableModel().getTotalSize();
		System.out.println("getEvents 3 tab size= " + tableSize);
		if (tableSize > 0) {
			for (int i = 0; i < tableSize; i++) {
				System.out.println("for " + i);
				event = new CalendarEvent();
				List<String> k = obtainRowsKey(i);
				event.key = keysList.get(0) + "-" + k.get(0);
				List<String> d = obtainRowsDate(i);
				// suponiendo que hay una propiedad fecha al menos
				event.start = d.get(0);
				event.end = (d.size() > 1) ? d.get(1) : "";
				event.title = obtainRowsTitle(i);
				calendarEvents.add(event);
			}
		}
		JSONArray jsonArray = new JSONArray();
		for (CalendarEvent event : calendarEvents) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("title", event.getTitle());
			jsonObject.put("start", event.getStart());
			jsonObject.put("end", event.getEnd());
			jsonObject.put("key", event.getKey());
			jsonArray.put(jsonObject);
		}
		System.out.println(jsonArray.toString());
		return jsonArray.toString();
	}

	// setear el filtro y obtener ambas fechas de rango
	private DateFilter setFilterForMonth(String month) {
		DateFilter df = new DateFilter();
		if (month.isEmpty()) {
			Date firstDayOfMonth = getFirstDayOfMonth("");
			Date lastDayOfMonth = getLastDayOfMonth("");
			df.setStart(firstDayOfMonth);
			df.setEnd(lastDayOfMonth);
			return df;
		} else {
			Date firstDayOfMonth = getFirstDayOfMonth(month);
			Date lastDayOfMonth = getLastDayOfMonth(month);
			df.setStart(firstDayOfMonth);
			df.setEnd(lastDayOfMonth);
			return df;
		}
	}

	private static Date getFirstDayOfMonth(String month) {
		Calendar calendar = Calendar.getInstance();
		if (!month.isEmpty())
			calendar.set(Calendar.MONTH, Integer.parseInt(month));
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.add(Calendar.DAY_OF_MONTH, -6);
		return calendar.getTime();
	}

	private static Date getLastDayOfMonth(String month) {
		Calendar calendar = Calendar.getInstance();
		if (!month.isEmpty())
			calendar.set(Calendar.MONTH, Integer.parseInt(month));
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);

		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DAY_OF_MONTH, 5);
		return calendar.getTime();
	}

	private static Tab getTab(HttpServletRequest request, String application, String module, String tabOject) {
		Tab tab = (Tab) getContext(request).get(application, module, tabOject);
		request.setAttribute("xava.application", application);
		request.setAttribute("xava.module", module);
		tab.setRequest(request);
		return tab;
	}
	
	protected org.openxava.view.View getView(HttpServletRequest request, String application, String module) { 
		View view = (View) getContext(request).get(application, module, "xava_view"); 
		request.setAttribute("xava.application", application);
		request.setAttribute("xava.module", module);
		view.setRequest(request);
		return view;
	}

	// obtener la fecha de la fila
	private List<String> obtainRowsDate(int row) {
		List<String> result = new ArrayList<>();
		int i = keysListSize;
		if (datesListSize == 0)
			return result;
		Object value = table.getValueAt(row, i);
		Object value2 = table.getValueAt(row, i + 1);
		// si la primera propiedad con nombre date o fecha es otra cosa, entonces
		// saltearlo
		if (verifyValue(value)) result.add(value.toString());
		// si tengo 2 fechas, validar tambien
		if (datesListSize == 2) {
			if (verifyValue(value2)) result.add(value2.toString());
		}
		return result;
	}

	// obtengo los datos de la fila pero antes aniadir lo que esta primero
	private String obtainRowsTitle(int row) {
		StringBuffer result = new StringBuffer();
		int i = properties1ListSize;
		int iColumn = keysListSize + datesListSize;
		int j = properties2ListSize;
		int jColumn = keysListSize + datesListSize + properties1ListSize;
		Object value;
		for (int k = 0; k < i; k++) {
			value = table.getValueAt(row, iColumn);
			if (verifyValue(value)) {
				if (result.length() > 0)
					result.append("-");
				result.append(tab.getMetaProperty(iColumn).getLabel());
				result.append(": ");
				result.append(format(iColumn, value));
			}
		}

		for (int k = 0; k < j; k++) {
			value = table.getValueAt(row, jColumn);
			if (verifyValue(value)) {
				if (result.length() > 0)
					result.append("-");
				result.append(tab.getMetaProperty(jColumn).getLabel());
				result.append(": ");
				result.append(format(jColumn, value));
			}
		}

		return result.toString();
	}

	private List<String> obtainRowsKey(int row) {
		// array para multiples key
		// actualmente solo uso 1 key y no puede ser nulo
		List<String> result = new ArrayList<>();
		int j = keysListSize;
		Object value;
		for (int i = 0; i < j; i++) {
			value = table.getValueAt(row, i);
			if (verifyValue(value)) {
				result.add(value.toString());
			}
		}
		return result;
	}
	
	private boolean verifyValue(Object value) {
		boolean b = true;
		if (!(value instanceof BigDecimal) && Is.empty(value))
			b = false;
		if (value instanceof byte[])
			b = false;
		return b;
	}

	private String format(int column, Object value) {
		MetaProperty p = tab.getMetaProperty(column);
		if (p.hasValidValues()) {
			return p.getValidValueLabel(value);
		} else if (p.getType().equals(boolean.class) || p.getType().equals(Boolean.class)) {
			return getBooleanFormatter().format(null, value);
		} else {
			return WebEditors.format(request, p, value, errors, view.getViewName(), true);
		}
	}

	private BooleanFormatter getBooleanFormatter() {
		if (booleanFormatter == null) {
			booleanFormatter = new BooleanFormatter();
		}
		return booleanFormatter;
	}

	private Tab setProperties(Tab tab) {
		// luego de clonar la tabla y hacer el filtro
		// obtener los keys en la primera columna (luego en multikey hacer un contador o
		// algo para designar la cantidad de columnas)
		// obtener los dates en la segunda y tercera (en multikey seran 2 columnas
		// despues de los key)
		// si no tiene 2 date, ver si puedo dejar columna vacia
		// luego ver si hay propiedades que se llamen asi como lo busco
		// de lo contrario, agarrar los primeros por default en la columna 4 5 6
		MetaModel mm = tab.getMetaTab().getMetaModel();
		List<String> newTabColumn = new ArrayList<>();
		List<String> expectedNames = Arrays.asList("name", "nombre", "title", "titulo", "description", "descripcion",
				"number", "numero");
		String[] datesName = { "date", "fecha" };

		keysList = new ArrayList<>(mm.getAllKeyPropertiesNames());
		List<String> datesList = new ArrayList<>();
		List<String> properties1List = new ArrayList<>();
		List<String> properties2List = new ArrayList<>();

		int mpCount = 0;
		int properties1ListCount = 0;
		int maxLimit = 6;

		newTabColumn.addAll(keysList);

		// lista de propiedades no calculadas y fecha
		List<MetaProperty> mp = tab.getMetaPropertiesNotCalculated();
		System.out.println(tab.getMetaPropertiesNotCalculated());
		for (MetaProperty metaProperty : mp) {
			System.out.println(metaProperty.getQualifiedName());
			// 2 columnas de fecha
			for (String name : datesName) {
				if (mpCount < 2 && metaProperty.getQualifiedName().contains(name)) {
					System.out.println("is date");
					// or metaProperty.getTypeName().contains(name)
					datesList.add(metaProperty.getQualifiedName());
					mpCount++;
				}
			}
			//columna para propiedades esperadas, usar maxLimit para no traer de mas
			if (properties1ListCount < maxLimit && expectedNames.contains(metaProperty.getQualifiedName())) {
				System.out.println("is expected");
				properties1List.add(metaProperty.getQualifiedName());
				properties1ListCount++;
			}
			//columna para las otras propiedades, segun orden, en caso de haber llegado a max no agregar
			//debo usar otra lista ya que el for va uno por uno
			if (properties1ListCount < maxLimit && 
					!datesList.contains(metaProperty.getQualifiedName()) && 
					!keysList.contains(metaProperty.getQualifiedName()) &&
					!properties1List.contains(metaProperty.getQualifiedName())) {
				System.out.println("isnt expected but added");
				properties2List.add(metaProperty.getQualifiedName());
			}

		}

		newTabColumn.addAll(datesList);
		newTabColumn.addAll(properties1List);
		// en caso que no encontro nada, agregar hasta la cantidad necesaria
		if (properties1ListCount < maxLimit) {
			for (int i = 0; i < properties1ListCount; i++) {
				newTabColumn.add(properties2List.get(i));
			}
			// newTabColumn.addAll(properties2List);
		} else {
			// sino limpiar para que el Size quede en 0
			properties2List.clear();
		}

		keysListSize = keysList.size();
		datesListSize = datesList.size();
		properties1ListSize = properties1List.size();
		properties2ListSize = properties2List.size();
		
		tab.clearProperties();
		tab.addProperties(newTabColumn);
		System.out.println(tab.getPropertiesNamesAsString());
		return tab;
	}

}
