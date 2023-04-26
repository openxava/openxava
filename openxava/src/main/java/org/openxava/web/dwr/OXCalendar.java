package org.openxava.web.dwr;

import java.math.*;
import java.rmi.*;
import java.time.*;
import java.util.*;

import javax.servlet.http.*;
import javax.swing.table.*;

import org.openxava.filters.*;
import org.openxava.formatters.*;
import org.openxava.model.meta.*;
import org.openxava.tab.Tab;
import org.openxava.util.*;
import org.openxava.view.View;
import org.openxava.web.*;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

import lombok.*;

@Getter
@Setter
public class OXCalendar extends DWRBase {

	transient private HttpServletRequest request;
	transient private HttpServletResponse response;
	private String application;
	private String module;
	private Messages errors;
	private View view;

	private Tab tab;
	private Tab tab2;
	private TableModel table;
	private DateFilter filter;

	private boolean dateWithTime;
	private boolean oldLib;
	private boolean hasCondition;
	private boolean hasFilter;
	private BooleanFormatter booleanFormatter;
	private CalendarEvent event;
	private String dateName;
	private String[] tabConditionValues;
	private String[] tabConditionComparators;

	private int keysListSize = 0;
	private int datesListSize = 0;
	private int properties1ListSize = 0;
	private int properties2ListSize = 0;
	private List<String> datesList = new ArrayList<>();

	public String getEvents(HttpServletRequest request, HttpServletResponse response, String application, String module,
			String monthYear) throws InterruptedException, RemoteException {
		System.out.println("dwr");
		this.application = application;
		this.module = module;
		this.response = response;
		view = getView(request, application, module);
		errors = new Messages();
		//initRequest(request, response, application, module); 
		List<CalendarEvent> calendarEvents = new ArrayList<>();
		System.out.println(application + " " + module);
		String tabObject = "xava_tab";
		System.out.println("antes de getTab " + application + module);
		System.out.println(request);
		tab2 = getTab(request, application, module, tabObject);
		//System.out.println(tab2.getModelName());
		//System.out.println(tab2.getPropertiesNamesAsString());
		//System.out.println(tab2.gettab);
		tab = tab2.clone();
		System.out.println("despues de getTab");
		System.out.println("antes de setDatesProperty tab size " + Arrays.toString(tab.getConditionValues()));
		setDatesProperty();
		
		hasCondition = tabHasCondition(tab);
		hasFilter = tab.getFilter() != null ? true : false;
		
//		if (tab.getConditionValues() != null) {
//			for (String condition : tab.getConditionValues()) {
//				if (condition!=null && !condition.equals("")) {
//					tabHasCondition = true;
//					System.out.println("tab tiene condiciones ");
//					break;
//				}
//			}
//		}
		
		if (tab.getFilter() != null) {
			System.out.println("tiene");
		} else {
			System.out.println("no tiene");
		}
		
		//si tiene filtros o condiciones no hara nada, traera todos los eventos
		if (!hasCondition && !hasFilter) {
			setFilter(tab, monthYear);
		}
		System.out.println("antes de setProperties tabsize " + tab.getTableModel().getTotalSize());
		tab = setProperties(tab);
		//System.out.println("antes de tablemodel " + tab.getTableModel().getTotalSize());
		
		//if (!hasCondition && hasFilter) setFilter(tab, monthYear);
		
		this.table = tab.getTableModel();
		int tableSize = 0;
		String json = null;

		try {
			tableSize = tab.getTableModel().getTotalSize();
			System.out.println("ingresando datos" + tableSize);
			if (tableSize > 0) {
				System.out.println("table size mayor a 0");
				for (int i = 0; i < tableSize; i++) {
					event = new CalendarEvent();
					//System.out.println("1");
					event.key = obtainRowsKey(i);
					//System.out.println("2");
					List<String> d = obtainRowsDate(i);
					//System.out.println("date " + d.get(0));
					event.start = d.get(0).split("_")[1];
					event.startName = d.get(0).split("_")[0];
					event.end = "";
					// event.end = (d.size() > 1) ? d.get(1).split("_")[1] : "";
					event.title = obtainRowsTitle(i);
					calendarEvents.add(event);
				}
			} else {
				event = new CalendarEvent();
				List<String> d = obtainRowsDate(-1);
				// no seria necesario, se supone que no debe ser vacio el primero (0)
				event.startName = d.get(0).split("_")[0].equals("") 
							  && !d.get(1).split("_")[0].equals("") 
								? d.get(1).split("_")[0] 
								: d.get(0).split("_")[0];
				calendarEvents.add(event);
			}
			ObjectMapper objectMapper = new ObjectMapper();
			json = objectMapper.writeValueAsString(calendarEvents);
		} catch (JsonProcessingException | RemoteException e) {
			System.out.println("error");
			e.printStackTrace();
		}
		Thread.sleep(5000);
		return json.toString();
	}

	private DateFilter setFilterForMonth(String monthYear) {
		DateFilter df = new DateFilter();
		
		String month = !monthYear.isEmpty() ? monthYear.split("_")[0] : "" ;
		String year = !monthYear.isEmpty() ? monthYear.split("_")[1] : "" ;
		
		System.out.println("entra a filtro" + dateWithTime + month.isEmpty() + month);
		Date firstDayOfMonth = getFirstDayOfMonth(month, year);
		Date lastDayOfMonth = getLastDayOfMonth(month, year);
		if (dateWithTime) {
			System.out.println("datewithtime");
			df.setStart(firstDayOfMonth);
			df.setEnd(lastDayOfMonth);
			System.out.println( firstDayOfMonth + " -- " + lastDayOfMonth);
		} else {
			System.out.println("date no withtime");
			Instant f = firstDayOfMonth.toInstant();
			Instant l = lastDayOfMonth.toInstant();
			LocalDate first = f.atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate last = l.atZone(ZoneId.systemDefault()).toLocalDate();
			//System.out.println( first + " -- " + last);
			df.setStart(first);
			df.setEnd(last);
		}
		System.out.println("termina filtro");
		return df;
	}

	private static Date getFirstDayOfMonth(String month, String year) {
		Calendar calendar = Calendar.getInstance();
		if (!month.isEmpty()) {
			calendar.set(Calendar.MONTH, Integer.parseInt(month));
			calendar.set(Calendar.YEAR, Integer.parseInt(year));
		}
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		calendar.add(Calendar.DAY_OF_MONTH, -6);
		System.out.println(calendar.getTime());
		return calendar.getTime();
	}

	private static Date getLastDayOfMonth(String month, String year) {
		Calendar calendar = Calendar.getInstance();
		if (!month.isEmpty()) {
			calendar.set(Calendar.MONTH, Integer.parseInt(month));
			calendar.set(Calendar.YEAR, Integer.parseInt(year));
		}
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);

		calendar.add(Calendar.DAY_OF_MONTH, 6);
		System.out.println(calendar.getTime());
		return calendar.getTime();
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

	// obtener la fecha de la fila
	private List<String> obtainRowsDate(int row) {
		
		List<String> result = new ArrayList<>();
		StringBuffer dateWithName = new StringBuffer();
		int i = keysListSize;
		 //System.out.println("keylistsize " + i);
		 //System.out.println(tab.getPropertiesNamesAsString());
		if (datesListSize == 0) return result;
		
		if (row == -1) {
			System.out.println("row -1");
			dateWithName.append(tab.getMetaProperty(i).getQualifiedName());
			dateWithName.append("_");
			result.add(dateWithName.toString());
			// no usado actualmente
			if (datesListSize == 2) {
				dateWithName = new StringBuffer();
				dateWithName.append(tab.getMetaProperty((i + 1)).getQualifiedName());
				dateWithName.append("_");
				dateWithName.append("");
				result.add(dateWithName.toString());
			}
		} else {
			Object value = table.getValueAt(row, i);
			Object value2 = table.getValueAt(row, (i + 1));
			DateFormatter df = new DateFormatter();
			// si la primera propiedad con nombre date o fecha es otra cosa, entonces
			// saltearlo
			// System.out.println("antes if");
			if (verifyValue(value)) {
				System.out.println(tab.getMetaProperty(i).getQualifiedName());
				System.out.println(value.toString());
				dateWithName.append(tab.getMetaProperty(i).getQualifiedName());
				dateWithName.append("_");
				dateWithName.append(df.formatCalendarEditor(value, dateWithTime, oldLib));
				result.add(dateWithName.toString());
			}
			// si tengo 2 fechas, validar tambien
			// aca hay que hacer algo para que se pueda seleccionar la fecha final, ya que
			// cualquiera puede ser la segunda fecha
			// en un principio conviene desactivar esto y dejar todo con inicio unicamente
			if (datesListSize == 2) {
				if (verifyValue(value2)) {
					dateWithName = new StringBuffer();
					dateWithName.append(tab.getMetaProperty((i + 1)).getQualifiedName());
					dateWithName.append("_");
					dateWithName.append(df.formatCalendarEditor(value2, dateWithTime, oldLib));
					// dateWithName.append(df.formatCalendarEditor(value2));
					result.add(dateWithName.toString());
				}
			}
		}
		 System.out.println("resultados");
		 System.out.println(result);
		return result;
	}

	// obtengo los datos de la fila pero antes aniadir lo que esta primero
	private String obtainRowsTitle(int row) {
		StringBuffer result = new StringBuffer();
		int i = properties1ListSize;
		int iColumn = keysListSize + datesListSize;
		int j = properties2ListSize;
		int jColumn = keysListSize + datesListSize + properties1ListSize;
		int currentColumn;
		Object value;
		for (int k = 0; k < i; k++) {
			currentColumn = iColumn + k;
			value = table.getValueAt(row, currentColumn);
			if (verifyValue(value)) {
				if (result.length() > 0)
					result.append(" - ");
				result.append(tab.getMetaProperty(currentColumn).getLabel());
				result.append(": ");
				result.append(format(currentColumn, value));
			}
		}
		System.out.println("after list 1 " + result.toString());
		System.out.println("next list size " + j);
		for (int k = 0; k < j; k++) {
			currentColumn = jColumn + k;
			System.out.println(currentColumn);
			value = table.getValueAt(row, currentColumn);
			System.out.println(value);
			if (verifyValue(value)) {
				if (result.length() > 0)
					result.append(" - ");
				result.append(tab.getMetaProperty(currentColumn).getLabel());
				result.append(": ");
				result.append(format(currentColumn, value));
			}
		}
		System.out.println("after list 2 " + result.toString());
		return result.toString();
	}

	private String obtainRowsKey(int row) {
		// array para multiples key
		// actualmente solo uso 1 key y no puede ser nulo
		List<String> result = new ArrayList<>();
		StringBuffer keys = new StringBuffer();
		int j = keysListSize;
		Object value;
		for (int i = 0; i < j; i++) {
			value = table.getValueAt(row, i);
			if (verifyValue(value)) {
				if (keys.length() > 0) {
					keys.append("_");
				}
				keys.append(value.toString());
			}
		}
		return keys.toString();
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

	private void setDatesProperty() {
		//List<MetaProperty> mp = tab.getMetaPropertiesNotCalculated();
		//List<String> mp = tab.getMetaTab().getMetaModel().getPropertiesNames();
		List<MetaProperty> mp = new ArrayList<>(tab.getMetaTab().getMetaModel().getMetaProperties());
		String[] datesName = { "date", "fecha" };
		int mpCount = 0;
		List<String> dateWithTimeList = Arrays.asList("java.util.Date", "java.time.LocalDateTime",
				"java.time.ZonedDateTime");

		System.out.println("entra a setDatesProperty");
		//System.out.println((tab.getMetaTab().getMetaModel().getPropertiesNames()));
		//System.out.println(tab.getMetaTab().getMetaModel().getMetaProperties());
		for (MetaProperty property : mp) {
			for (String name : datesName) {
				//System.out.println("name " + property.getName());
				//if (mpCount < 2 && property.getQualifiedName().contains(name)) {
				if (mpCount < 2 && property.getName().contains(name)) {
					// or metaProperty.getTypeName().contains(name)
					System.out.println("name " + mpCount + " " + property.getName() + "  "  + property.getTypeName());
					// por el momento se esta usando el primer date
					if (mpCount == 0 && !property.getName().contains(".")) dateName = property.getName();
					datesList.add(property.getName());
					//if (mpCount == 0 && !property.getQualifiedName().contains(".")) dateName = property.getQualifiedName();
					//datesList.add(property.getQualifiedName());					
					mpCount++;
					
					//ambos booleanos
					dateWithTime = dateWithTimeList.contains(property.getTypeName()) ? true : false;
					// por el momento se usara para filtrar solo java.util y java.time
					String className = property.getTypeName();
					if (className.startsWith("java.util.")) {
						oldLib = true;
					} else if (className.startsWith("java.time.")) {
						oldLib = false;
					}
					// oldLib = (className.startsWith("java.util.")) ? true : false;
					// System.out.println(className + " es una subclase de java.util.Date");
				}
			}

		}
		System.out.println("termina");
	}

	private Tab setProperties(Tab tab) throws RemoteException {
		// luego de clonar la tabla y hacer el filtro
		// obtener los keys en la primera columna (luego en multikey hacer un contador o
		// algo para designar la cantidad de columnas)
		// obtener los dates en la segunda y tercera (en multikey seran 2 columnas
		// despues de los key)
		// si no tiene 2 date, ver si puedo dejar columna vacia
		// luego ver si hay propiedades que se llamen asi como lo busco
		// de lo contrario, agarrar los primeros por default en la columna 4 5 6
		// MetaModel mm = tab.getMetaTab().getMetaModel();
		List<String> newTabColumn = new ArrayList<>();
		List<String> expectedNames = Arrays.asList("name", "nombre", "title", "titulo", "description", "descripcion",
				"number", "numero");
		List<String> keysList = new ArrayList<>(tab.getMetaTab().getMetaModel().getAllKeyPropertiesNames());
		List<String> properties1List = new ArrayList<>();
		List<String> properties2List = new ArrayList<>();
		int properties1ListCount = 0;
		int properties2ListCount = 0;
		int maxLimit = 2;

		newTabColumn.addAll(keysList);
		System.out.println("1 tab " + tab.getTableModel().getTotalSize());
		// lista de propiedades no calculadas y fecha
		List<MetaProperty> mp = tab.getMetaPropertiesNotCalculated();
		//System.out.println(tab.getMetaPropertiesNotCalculated());
		//System.out.println("2");
		for (MetaProperty metaProperty : mp) {
			// System.out.println(metaProperty.getQualifiedName());
			// 2 columnas de fecha
//			for (String name : datesName) {
//				if (mpCount < 2 && metaProperty.getQualifiedName().contains(name)) {
//					// or metaProperty.getTypeName().contains(name)
//					//System.out.println(metaProperty.getTypeName());
//					if (mpCount == 0) dateName = metaProperty.getQualifiedName(); 
//					datesList.add(metaProperty.getQualifiedName());
//					mpCount++;
//					//dateWithTime = dateWithTimeList.contains(metaProperty.getQualifiedName()) ? true : false;
//				}
//			}
			// columna para propiedades esperadas, usar maxLimit para no traer de mas
			if (properties1ListCount < maxLimit && expectedNames.contains(metaProperty.getQualifiedName())) {
				properties1List.add(metaProperty.getQualifiedName());
				properties1ListCount++;
				properties2ListCount++;
				System.out.println(metaProperty.getQualifiedName() + " +1 = " + properties1ListCount);
			}
			System.out.println("2 tab " + tab.getTableModel().getTotalSize());
			// columna para las otras propiedades, segun orden, en caso de haber llegado a
			// max no agregar
			// debo usar otra lista ya que el for va uno por uno
			//System.out.println(properties2ListCount + " ? " + properties1ListCount);
			if (properties2ListCount < maxLimit && !datesList.contains(metaProperty.getQualifiedName())
					&& !keysList.contains(metaProperty.getQualifiedName())
					&& !properties1List.contains(metaProperty.getQualifiedName())) {
				System.out.println("< max " + properties2ListCount + " = " + metaProperty.getQualifiedName() );
				properties2List.add(metaProperty.getQualifiedName());
				properties2ListCount++;
			}
			System.out.println("3 tab " + tab.getTableModel().getTotalSize());
		}
		 System.out.println("3");
		newTabColumn.addAll(datesList);
		newTabColumn.addAll(properties1List);
		// en caso que no encontro nada, agregar hasta la cantidad necesaria

		if (properties1ListCount < maxLimit) {
			int j = 0;
			for (int i = properties1ListCount; i < maxLimit; i++) {
				newTabColumn.add(properties2List.get(j));
				j++;
			}
			properties2ListSize = j;
		} else {
			properties2ListSize = 0;
		}
		
		System.out.println("4 tab " + tab.getTableModel().getTotalSize());

		keysListSize = keysList.size();
		datesListSize = datesList.size();
		properties1ListSize = properties1List.size();
		//properties2ListSize = properties2List.size();
		//System.out.println("4");
		System.out.println("4 tab antes de clean " + tab.getTableModel().getTotalSize());

		//tab.clearProperties();
		//System.out.println("4 tab despues de clean " + tab.getTableModel().getTotalSize());
		//no funciona con condiciones
		//tab.addProperties(newTabColumn);
		
		if (hasCondition) {
			System.out.println("tiene condicion antes de aniadir propiedades " + tab.getTableModel().getTotalSize() + "-" + Arrays.toString(tab.getConditionValues()));
			System.out.println("count " + tab.getConfigurationsCount());

			String[] conditionValues = tab.getConditionValues();
			String[] conditionValuesTo = tab.getConditionValuesTo();
			String[] conditionComparators = tab.getConditionComparators();
			
			tab.addProperties(newTabColumn, conditionValues, conditionValuesTo, conditionComparators);
			
//			for (int i = 0; i<newTabColumn.size(); i++) {
//				tab.addProperty(i, newTabColumn.get(i));
//			}
			System.out.println("tiene condicion despues de aniadir propiedades " + tab.getTableModel().getTotalSize() + "-" + Arrays.toString(tab.getConditionValues()));
		} else {
			tab.clearProperties();
			tab.addProperties(newTabColumn);
		}
		
		
		//System.out.println(keysListSize + " " + datesListSize + " " + properties1ListSize + " " + properties2ListSize);
		// System.out.println(newTabColumn);
		System.out.println(tab.getPropertiesNamesAsString());
		
		return tab;
	}
	
	private boolean tabHasCondition(Tab tab) {
		boolean b = false;
		if (tab.getConditionValues() != null) {
			for (String condition : tab.getConditionValues()) {
				if (condition!=null && !condition.equals("")) {
					b = true;
					break;
				}
			}
		}
		return b;
	}

	private void setFilter(Tab tab, String monthYear) {
		DateFilter filter = new DateFilter();
		filter = setFilterForMonth(monthYear);
		System.out.println("antes de setFilter con dname " + dateName);
		tab.setFilter(filter);
		// hay que cambiar despues por ${}
		tab.setBaseCondition("${" + dateName + "} between ? and ?");
	}

}
