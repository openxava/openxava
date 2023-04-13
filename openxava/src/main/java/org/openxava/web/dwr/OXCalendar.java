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
	private static Messages errors;
	private static View view;
	private CalendarEvent event;
	private CalendarEventIterator ci;

	int keysListSize = 0;
	int datesListSize = 0;
	int properties1ListSize = 0;
	int properties2ListSize = 0;

	public static void setOX(View view, Messages errors) {
		OXCalendar.view = view;
		OXCalendar.errors = errors;
	}

	// List<CalendarEvent>
	public String getEvents(HttpServletRequest request, HttpServletResponse response, String application, String module,
			String month) throws RemoteException {
		long ini = System.currentTimeMillis();
		System.out.println("dwr");
		this.application = application;
		this.module = module;
		this.response = response;

//		System.out.println(request);
//		System.out.println(application);
//		System.out.println(module);
		// initRequest(request, response, application, module);

		List<CalendarEvent> calendarEvents = new ArrayList<>();

		System.out.println("getEvents 1 mes = " + month);

		try {
			// System.out.println("entra try");
			// se debe hacer un clon del tab para trabajar sobre ella
			// de lo contrario, el tab siempre se filtrara sobre el mismo, es decir, se
			// aplicara
			// un filtro sobre otro filtro y no sobre la tabla completa
			String tabObject = "xava_tab";
			// long ini = System.currentTimeMillis();
			tab2 = getTab(request, application, module, tabObject);
			// System.out.println("getEvents 3 tab cuesta= " + cuesta);
			tab = tab2.clone();
			// System.out.println("getEvents 2 cl count" +
			// tab.getTableModel().getColumnCount());
			tab.clearProperties();
			tab.addProperty("id");

			// sin clonar
//			String tabObject = "xava_tab";
//			tab = getTab(request, application, module, tabObject);
//			tab.reset();
			// esto esta haciendo la consulta jpa
			// System.out.println("getEvents after reset" +
			// tab.getTableModel().getTotalSize());
			// tab.clearCondition();

			// obtener el filtro del mes actual o del mes recibido
			DateFilter filter = new DateFilter();
			filter = setFilterForMonth(month);
			// settear el filtro con basecondition al clon de la tabla
			tab.setFilter(filter);
			tab.setBaseCondition("date between ? and ?");
			// tab.filter();

			tab = setProperties(tab);

			// System.out.println("getEvents 2 cl count" +
			// tab.getTableModel().getColumnCount());
			this.table = tab.getTableModel();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("ha fallado");
		}

		// obtener todos los eventos del tab clonado
		// como ya se como es la tabla ahora, uso los size para determinar que columna
		// agregar

		int tableSize = 0;
		tableSize = tab.getTableModel().getTotalSize();

		System.out.println("getEvents 3 tab size= " + tableSize);
		System.out.println("getEvents 3 tab size= " + tab.getPropertiesNamesAsString());
		System.out.println("getEvents 3 tab size= " + tab.getMetaTab().getPropertiesNames());
		// System.out.println("getEvents 4");
		if (tableSize > 0) {
			for (int i = 0; i < tableSize; i++) {
				System.out.println("for " + i);
				event = new CalendarEvent();
				List<String> k = obtainRowsKey(i);
				event.key = k.get(0);
				List<String> d = obtainRowsDate(i);
				// suponiendo que hay una propiedad fecha al menos
				event.start = d.get(0);
				event.end = (d.size() > 1) ? d.get(1) : "";
				event.title = obtainRowsTitle(i);
				calendarEvents.add(event);
			}
		}
		// System.out.println("getEvents 5");
		JSONArray jsonArray = new JSONArray();
		for (CalendarEvent event : calendarEvents) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("title", event.getTitle());
			jsonObject.put("start", event.getStart());
			jsonObject.put("end", event.getEnd());
			// cambiar row por key despues
			jsonObject.put("key", event.getKey());
			jsonArray.put(jsonObject);
		}
		System.out.println(jsonArray.toString());
		long cuesta = System.currentTimeMillis() - ini;
		System.out.println("getEvents tarda " + cuesta);
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
			System.out.println("primer return");
			return df;
		} else {
			// System.out.println("View month: " + month);
			Date firstDayOfMonth = getFirstDayOfMonth(month);
			Date lastDayOfMonth = getLastDayOfMonth(month);
			df.setStart(firstDayOfMonth);
			df.setEnd(lastDayOfMonth);
			// System.out.println("View month: " + firstDayOfMonth + "..." +
			// lastDayOfMonth);
			System.out.println("segundo return");
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

		// obtener hasta 6 dias antes del mes indicado
		// calendar.add(Calendar.DAY_OF_MONTH, -6);

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

		// obtener hasta 6 dias despues del mes indicado, para vistas semanales
		// calendar.add(Calendar.MONTH, 1);
		// calendar.add(Calendar.DAY_OF_MONTH, 5);
		return calendar.getTime();
	}

	private static Tab getTab(HttpServletRequest request, String application, String module, String tabOject) {
		// Tab tab = new Tab();
		Tab tab = (org.openxava.tab.Tab) getContext(request).get(application, module, tabOject);
		request.setAttribute("xava.application", application);
		request.setAttribute("xava.module", module);
		tab.setRequest(request);
		return tab;
	}

	/*
	 * //setear tab con el filtro private void setTabWithFilter(Tab tab, DateFilter
	 * filter) { tab.setFilter(filter); }
	 */

	// obtener la fecha para start
	// nunca deberia ser nulo ya que en el editor preguntamos si tiene un elemento
	// que sea date, pero preguntamos igual
	// doble verificacion para que ese nombre "fecha, date" no sea de otro tipo de
	// elemento
	private List<String> obtainRowsDate(int row) {
		List<String> result = new ArrayList<>();
		int i = keysListSize;
		if (datesListSize == 0)
			return result;
		Object value = table.getValueAt(row, i);
		Object value2 = table.getValueAt(row, i + 1);
		// si la primera propiedad con nombre date o fecha es otra cosa, entonces
		// saltearlo
		if (verifyValue(value))
			result.add(value.toString());
		// result.add(verifyValue(value) ? value.toString() : "");

		// si tengo 2 fechas, en caso que no haya, result 1 ya es null
		if (datesListSize == 2) {
			if (verifyValue(value2))
				result.add(value2.toString());
			// result.add(verifyValue(value2) ? value2.toString() : "");
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

	private int indexForAny(int ordinal, String... properties) {
		int timesFound = 0;
		// System.out.println(Arrays.toString(properties));
		for (String propertyName : properties) {
			// System.out.println("pn " + propertyName);
			int idx = 0;
			for (MetaProperty metaProperty : tab.getMetaProperties()) {
				// System.out.println("tn " + metaProperty.getQualifiedName());
				if (metaProperty.getQualifiedName().contains(propertyName)) {

					// siempre tira java.util.Date
					// System.out.println("contiene");
					if (++timesFound == ordinal) {
						// System.out.println("equal");
						return idx;
					}
				}
				idx++;
			}
		}
		return -1;
	}

	// obtengo los datos de la fila pero antes aniadir lo que esta primero
	private String obtainRowsTitle(int row) {
		StringBuffer result = new StringBuffer();
		int i = properties1ListSize;
		int iColumn = keysListSize + datesListSize;
		int j = properties2ListSize;
		int jColumn = keysListSize + datesListSize + properties1ListSize;
		Object value;
		Object value2;
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

	// obtener lo que tenga que estar primero, sino uso el primero por defecto
	private String obtainFirstContentForTitle(int row) {
		String result = "";
		int idx = 0;
		// este if se usa si se busca mas de una propiedad
		// de fabrica es -1
		if (titleFirstIdx < 0) {
			titleFirstIdx = indexForAny(1, "name", "nombre", "title", "titulo", "description", "descripcion", "number",
					"numero", "id");
			// si no encuentra ninguna propiedad anterior, agarra el primero
			// no se esta contemplando la tabla vacia aun
			idx = (titleFirstIdx < 0 && !tab.getMetaProperties().isEmpty()) ? 0 : titleFirstIdx;
		}
		// int idx = titleFirstIdx;
		// if (idx >= 0) {
		Object value = table.getValueAt(row, idx);
		if (!(value instanceof BigDecimal) && Is.empty(value))
			return result;
		if (value instanceof byte[])
			return result;
		result = tab.getMetaProperty(idx).getLabel() + ": " + format(idx, value);
		// }
		// System.out.println(t);
		return result;
	}

	private String format(int column, Object value) {
		MetaProperty p = tab.getMetaProperty(column);
		if (p.hasValidValues()) {
			// System.out.println("primer return");
			return p.getValidValueLabel(value);
		} else if (p.getType().equals(boolean.class) || p.getType().equals(Boolean.class)) {
			// System.out.println("segundo return");
			return getBooleanFormatter().format(null, value);
		} else {
			// System.out.println("tercer return");
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

		List<String> keysList = new ArrayList<>(mm.getAllKeyPropertiesNames());
		List<String> datesList = new ArrayList<>();
		List<String> properties1List = new ArrayList<>();
		List<String> properties2List = new ArrayList<>();

		int mpCount = 0;
		int properties1ListCount = 0;
		int properties2ListCount2 = 0;

		newTabColumn.addAll(keysList);

		// lista de propiedades no calculadas y fecha
		List<MetaProperty> mp = tab.getMetaPropertiesNotCalculated();

		for (MetaProperty metaProperty : mp) {
			for (String name : datesName) {
				if (mpCount < 2 && metaProperty.getQualifiedName().contains(name)) {
					// or metaProperty.getTypeName().contains(name)
					datesList.add(metaProperty.getQualifiedName());
					mpCount++;
				}
			}
			// por defecto estamos usando solo 1 propiedad
			if (properties1ListCount < 1 && expectedNames.contains(metaProperty.getQualifiedName())) {
				properties1List.add(metaProperty.getQualifiedName());
				properties1ListCount++;
			}
			// no hago contador, sino que agrego todos, luego decido sumarlo al tab o no
			// dependiendo si properties1List encontro algo pero sigue siendo menor que
			// properties1ListCount
			if (!datesList.contains(metaProperty.getQualifiedName())
					&& !keysList.contains(metaProperty.getQualifiedName())) {
				properties2List.add(metaProperty.getQualifiedName());
			}

		}

		newTabColumn.addAll(datesList);
		newTabColumn.addAll(properties1List);
		// en caso que no encontro nada, agregar hasta la cantidad necesaria
		if (properties1ListCount < 1) {
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
		System.out.println(keysListSize + " " + datesListSize + " " + properties1ListSize + " " + properties2ListSize);

		tab.clearProperties();
		tab.addProperties(newTabColumn);
		System.out.println(Arrays.toString(newTabColumn.toArray()));
		return tab;
	}

}
