package org.openxava.web.dwr;

import java.math.*;
import java.rmi.*;
import java.text.*;
import java.time.*;
import java.time.format.*;
import java.util.*;

import javax.servlet.http.*;
import javax.swing.table.*;

import org.apache.commons.logging.*;
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

/**
 * 
 * @since 7.1
 * @author Chungyen Tsai
 */

@Getter
@Setter
public class Calendar extends DWRBase {
	
	private static Log log = LogFactory.getLog(Calendar.class);
	
	transient private HttpServletRequest request;
	transient private HttpServletResponse response;
	private String application;
	private String module;
	private Messages errors;
	private View view;

	private Tab tab;
	private Tab tab2;
	private TableModel table;
	private DateRangeFilter filter;

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
			String monthYear) throws RemoteException, JsonProcessingException{
		System.out.println("dwr");
		this.application = application;
		this.module = module;
		this.response = response;
		view = getView(request, application, module);
		errors = new Messages();
		
		List<CalendarEvent> calendarEvents = new ArrayList<>();
		String tabObject = "xava_tab";
		tab2 = getTab(request, application, module, tabObject);
		tab = tab2.clone();

		setDatesProperty();
		hasCondition = tabHasCondition(tab);
		hasFilter = tab.getFilter() != null ? true : false;
		if (!hasCondition && !hasFilter) {
			setFilter(tab, monthYear);
		}
		tab = setProperties(tab);
		
		this.table = tab.getTableModel();
		int tableSize = 0;
		String json = null;
		
		tableSize = tab.getTableModel().getTotalSize();
		System.out.println("ingresando datos" + tableSize);
		if (tableSize > 0) {
			System.out.println("table size mayor a 0");
			for (int i = 0; i < tableSize; i++) {
				event = new CalendarEvent();
				event.key = obtainRowsKey(i);
				List<String> d = obtainRowsDate(i);
				event.start = d.get(0).split("_")[1];
				event.startName = d.get(0).split("_")[0];
				event.end = "";
				// para usarse en rango de fecha
				// event.end = (d.size() > 1) ? d.get(1).split("_")[1] : "";
				event.title = obtainRowsTitle(i);
				calendarEvents.add(event);
			}
		} else {
			event = new CalendarEvent();
			List<String> d = obtainRowsDate(-1);
			// aunque no es necesario, ya que se supone que no debe ser vacio el primero (0), y el segundo esta usado para la segunda fecha
			String f = d.get(0).split("_")[0];			
			String s = d.size() > 1 ? d.get(1).split("_")[0] : "";
			event.startName = f.equals("")  && !s.equals("") ? s : f;
			calendarEvents.add(event);
		}
		ObjectMapper objectMapper = new ObjectMapper();
		json = objectMapper.writeValueAsString(calendarEvents);
		return json.toString();
	}

	private DateRangeFilter setFilterForMonth(String monthYear) {
		DateRangeFilter df = new DateRangeFilter();
		String month = !monthYear.isEmpty() ? monthYear.split("_")[0] : "" ;
		String year = !monthYear.isEmpty() ? monthYear.split("_")[1] : "" ;

		Date firstDayOfMonth = getFirstDayOfMonth(month, year);
		Date lastDayOfMonth = getLastDayOfMonth(month, year);
		if (dateWithTime) {
			df.setStart(firstDayOfMonth);
			df.setEnd(lastDayOfMonth);
		} else {
			Instant f = firstDayOfMonth.toInstant();
			Instant l = lastDayOfMonth.toInstant();
			LocalDate first = f.atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate last = l.atZone(ZoneId.systemDefault()).toLocalDate();
			df.setStart(first);
			df.setEnd(last);
		}
		return df;
	}

	private static Date getFirstDayOfMonth(String month, String year) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		if (!month.isEmpty()) {
			calendar.set(java.util.Calendar.MONTH, Integer.parseInt(month));
			calendar.set(java.util.Calendar.YEAR, Integer.parseInt(year));
		}
		calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
		calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
		calendar.set(java.util.Calendar.MINUTE, 0);
		calendar.set(java.util.Calendar.SECOND, 0);
		calendar.set(java.util.Calendar.MILLISECOND, 0);

		calendar.add(java.util.Calendar.DAY_OF_MONTH, -6);
		return calendar.getTime();
	}

	private static Date getLastDayOfMonth(String month, String year) {
		java.util.Calendar calendar = java.util.Calendar.getInstance();
		if (!month.isEmpty()) {
			calendar.set(java.util.Calendar.MONTH, Integer.parseInt(month));
			calendar.set(java.util.Calendar.YEAR, Integer.parseInt(year));
		}
		calendar.set(java.util.Calendar.DAY_OF_MONTH, calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH));
		calendar.set(java.util.Calendar.HOUR_OF_DAY, 23);
		calendar.set(java.util.Calendar.MINUTE, 59);
		calendar.set(java.util.Calendar.SECOND, 59);
		calendar.set(java.util.Calendar.MILLISECOND, 999);

		calendar.add(java.util.Calendar.DAY_OF_MONTH, 6);
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

	private List<String> obtainRowsDate(int row) {
		List<String> result = new ArrayList<>();
		StringBuffer dateWithName = new StringBuffer();
		int i = keysListSize;
		if (datesListSize == 0) return result;
		
		if (row == -1) {
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
			// si la primera propiedad con nombre date o fecha es otra cosa, entonces
			// saltearlo
			if (verifyValue(value)) {
				dateWithName.append(tab.getMetaProperty(i).getQualifiedName());
				dateWithName.append("_");
				dateWithName.append(format(value, dateWithTime, oldLib));
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
					dateWithName.append(format(value2, dateWithTime, oldLib));
					result.add(dateWithName.toString());
				}
			}
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
		int currentColumn;
		Object value;
		for (int k = 0; k < i; k++) {
			currentColumn = iColumn + k;
			value = table.getValueAt(row, currentColumn);
			if (verifyValue(value)) {
				if (result.length() > 0) result.append(" - ");
				result.append(tab.getMetaProperty(currentColumn).getLabel());
				result.append(": ");
				result.append(format(currentColumn, value));
			}
		}
		for (int k = 0; k < j; k++) {
			currentColumn = jColumn + k;
			value = table.getValueAt(row, currentColumn);
			if (verifyValue(value)) {
				if (result.length() > 0) result.append(" - ");
				result.append(tab.getMetaProperty(currentColumn).getLabel());
				result.append(": ");
				result.append(format(currentColumn, value));
			}
		}
		return result.toString();
	}

	private String obtainRowsKey(int row) {
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
		List<MetaProperty> mp = new ArrayList<>(tab.getMetaTab().getMetaModel().getMetaProperties());
		String[] datesName = { "date", "fecha" };
		int mpCount = 0;
		List<String> dateWithTimeList = Arrays.asList("java.util.Date", "java.time.LocalDateTime",
				"java.time.ZonedDateTime");

		for (MetaProperty property : mp) {
			for (String name : datesName) {
				if (mpCount < 2 && property.getName().toLowerCase().contains(name)) {
					System.out.println("name " + mpCount + " " + property.getName() + "  "  + property.getTypeName());
					// por el momento se esta usando el primer date
					if (mpCount == 0 && !property.getName().contains(".")) dateName = property.getName();
					datesList.add(property.getName());			
					mpCount++;
					
					//ambos booleanos
					dateWithTime = dateWithTimeList.contains(property.getTypeName()) ? true : false;
					// por el momento se usara para filtrar solo java.util y java.time
					// se filtran por java util y java time como libreria vieja y nueva
					String className = property.getTypeName();
					if (className.startsWith("java.util.")) {
						oldLib = true;
					} else if (className.startsWith("java.time.")) {
						oldLib = false;
					} 
				}
			}
		}
	}

	private Tab setProperties(Tab tab) {
		// luego de clonar la tabla y hacer el filtro
		// obtener los keys en las primeras columnas
		// obtener los dates para las segundas columnas
		// si no tiene 2 date, solamente habra 1 columna
		// luego ver si hay propiedades que se llamen como lo esperado
		// de lo contrario, agarrar los primeros por default en las terceras columnas
		// generalmente las propiedades esperadas estan en el tab por defecto
		// por lo cual se usa getMetaPropertiesNotCalculated para traer las propiedades del tab unicamente
		// no se traen las propiedades calculadas debido al costo de la consulta

		List<String> newTabColumn = new ArrayList<>();
		List<String> expectedNames = Arrays.asList("name", "nombre", "title", "titulo", "description", "descripcion",
				"number", "numero");
		List<String> keysList = new ArrayList<>(tab.getMetaTab().getMetaModel().getAllKeyPropertiesNames());
		List<String> properties1List = new ArrayList<>();
		List<String> properties2List = new ArrayList<>();
		int properties1ListCount = 0;
		int properties2ListCount = 0;
		// por momento se traen hasta 2 propiedades
		int maxLimit = 2;

		newTabColumn.addAll(keysList);
		List<MetaProperty> mp = tab.getMetaPropertiesNotCalculated();
		for (MetaProperty metaProperty : mp) {
			// columna para propiedades esperadas, usar maxLimit para no traer de mas
			if (properties1ListCount < maxLimit && expectedNames.contains(metaProperty.getQualifiedName())) {
				properties1List.add(metaProperty.getQualifiedName());
				properties1ListCount++;
				properties2ListCount++;
			}
			// columna para las otras propiedades, en caso de haber llegado a max no agregar mas
			// debo usar otra lista ya que el for va uno por uno
			if (properties2ListCount < maxLimit && !datesList.contains(metaProperty.getQualifiedName())
					&& !keysList.contains(metaProperty.getQualifiedName())
					&& !properties1List.contains(metaProperty.getQualifiedName())) {
				properties2List.add(metaProperty.getQualifiedName());
				properties2ListCount++;
			}
		}
		newTabColumn.addAll(datesList);
		newTabColumn.addAll(properties1List);

		// si la lista1 es menor que maxLimit significa que queda mas lugar para agregar, agrego la segunda lista
		if (properties1ListCount < maxLimit) {
			int j = 0;
			for (int i = properties1ListCount; i < maxLimit; i++) {
				newTabColumn.add(properties2List.get(j));
				j++;
			}
			properties2ListSize = j;
		} else {
			// si ya no hay mas lugar, lista2size = 0
			properties2ListSize = 0;
		}

		keysListSize = keysList.size();
		datesListSize = datesList.size();
		properties1ListSize = properties1List.size();
		
		if (hasCondition) {
			String[] conditionValues = tab.getConditionValues();
			String[] conditionValuesTo = tab.getConditionValuesTo();
			String[] conditionComparators = tab.getConditionComparators();
			tab.addProperties(newTabColumn, conditionValues, conditionValuesTo, conditionComparators);
		} else {
			tab.clearProperties();
			tab.addProperties(newTabColumn);
		}
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
		DateRangeFilter filter = new DateRangeFilter();
		filter = setFilterForMonth(monthYear);
		tab.setFilter(filter);
		tab.setBaseCondition("${" + dateName + "} between ? and ?");
	}
	
	public String format(Object date, boolean withTime, boolean oldLib) {
		String format;
		if (date == null) return "";
		if (date instanceof String || date instanceof Number) return date.toString();
		// si es libreria vieja, usa SimpleDateFormat, de lo contrario usa DateTimeFormatter 
		// tambien debe diferenciar si el date fuente viene con horario o no.
		// este resultado se usa para ubicar el evento en el calendario
		if (oldLib) {
			format = withTime ? "yyyy-MM-dd HH:mm:ss" : "yyyy-MM-dd";
			DateFormat df = new SimpleDateFormat(format);
			return df.format(date);
		} else {
			DateTimeFormatter formatter = withTime ? DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") : DateTimeFormatter.ofPattern("yyyy-MM-dd");
			return withTime ? ((LocalDateTime)date).format(formatter) : ((LocalDate) date).format(formatter);
		}
	}

}
