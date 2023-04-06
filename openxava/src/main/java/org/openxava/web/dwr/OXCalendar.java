package org.openxava.web.dwr;

import java.math.*;
import java.rmi.*;
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
import org.openxava.web.editors.*;

import lombok.*;

@Getter @Setter
public class OXCalendar extends DWRBase {

	transient private HttpServletRequest request;
	transient private HttpServletResponse response;
	private String application;
	private String module;
	private List<CalendarEvent> calendarEvents;
	private Tab tab;
	private DateFilter filter;
	private TableModel table;
	private String date2;
	int titleFirstIdx = -1;
	private BooleanFormatter booleanFormatter; 
	private static Messages errors;
	private static View view;
	
	public static void setData(View view, Messages errors) {
		OXCalendar.view = view;
		OXCalendar.errors = errors;
	}
	
	public List<CalendarEvent> getEvents(HttpServletRequest request, 
			HttpServletResponse response, 
			String application, 
			String module) throws RemoteException {
			initRequest(request, response, application, module); 
			this.application = application;
			this.module = module;
			this.table = tab.getTableModel();
			
			System.out.println("getEvents 1");
			//primero traer con el filtro del mes actual
			filter = setFilterForMonth();
			System.out.println("getEvents 2");
			//obtener tab y settear el tab con el filtro
			String tabObject = "xava_tab";
			tab = getTab(request, application, module, tabObject);
			tab.setFilter(filter);
			
			//obtener todos los eventos del tab
			System.out.println("getEvents 3");
			int tableSize = 0;
			tableSize = tab.getTableModel().getTotalSize();
			if (tableSize > 0) {
				//System.out.println("tabSize");
				for (int i = 0; i<tableSize; i++) {
					//System.out.println("for");
					CalendarEvent event = new CalendarEvent();
					event.setStart(obtainRowsDate(i));
					event.setEnd(date2);
					event.setTitle(obtainContent(i));
					event.setRow(Integer.toString(i));
					calendarEvents.add(event);
				}
			}
			System.out.println(Arrays.toString(calendarEvents.toArray()));
			
			return calendarEvents;
	}

	public void test(HttpServletRequest request, HttpServletResponse response, String application, String module) {
		this.application = application;
		this.module = module;
		System.out.println(" Aloha: " + application);
		System.out.println(" Aloha: " + module);
		//Tab tab = getTab(request, application, module);
		//System.out.println(tab.getTotalSize());
	}

	//setear el filtro y obtener ambas fechas de rango
	private DateFilter setFilterForMonth() {
		DateFilter dateFilter = new DateFilter();
		Calendar calendar = Calendar.getInstance();
		Date firstDayOfMonth = getFirstDayOfMonth(calendar.getTime());
		Date lastDayOfMonth = getLastDayOfMonth(calendar.getTime());
		dateFilter.setStart(firstDayOfMonth);
		dateFilter.setEnd(lastDayOfMonth);
		return dateFilter;
	}

	private static Date getFirstDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}

	private static Date getLastDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		return calendar.getTime();
	}
	
	private static Tab getTab(HttpServletRequest request, String application, String module, String tabOject) {
		Tab tab = (org.openxava.tab.Tab) getContext(request).get(application, module, tabOject);
		request.setAttribute("xava.application", application);
		request.setAttribute("xava.module", module);
		tab.setRequest(request);
		return tab;
	}

	/*
	//setear tab con el filtro
	private void setTabWithFilter(Tab tab, DateFilter filter) {
		tab.setFilter(filter);
	}*/
	
	// obtener la fecha para start
	private String obtainRowsDate(int row) {
		String t = "";
		int idx = -1;
		idx = indexForAny(1, "date", "java.util.Date", "Date");
		if (idx < 0 && !tab.getMetaProperties().isEmpty()) return "";
		if (idx > 0) {
			Object value = table.getValueAt(row, idx);
			if (!(value instanceof BigDecimal) && Is.empty(value))
				return t;
			if (value instanceof byte[])
				return t;
			t = value.toString();
			
			//System.out.println(t);
			//seteo la fecha end
			String[] s = t.split(" ");
			String d = s[1].replaceAll("0", "1");
			date2 = s[0] + " " + d;
			
		}
		return t;
	}
	
	private int indexForAny(int ordinal, String... properties) {
		int timesFound = 0;
		//System.out.println(Arrays.toString(properties));
		for (String propertyName : properties) {
			//System.out.println("pn " + propertyName);
			int idx = 0;
			for (MetaProperty metaProperty : tab.getMetaProperties()) {
				//System.out.println("tn " + metaProperty.getQualifiedName());
				if (metaProperty.getQualifiedName().contains(propertyName)) {
					
					// siempre tira java.util.Date
					//System.out.println("contiene");
					if (++timesFound == ordinal) {
						//System.out.println("equal");
						return idx;
					}
				}
				idx++;
			}
		}
		return -1;
	}
	
	// obtengo los datos de la fila pero antes aniadir lo que esta primero
	private String obtainContent(int row) {
		StringBuffer result = new StringBuffer();
		int c = 0;
		//obtener solo 1 elemento luego del titulo
		String f = obtainFirstContentForTitle(row);
		if (f.length() > 1)
			result.append(f);
		for (int column = 0; column < table.getColumnCount(); column++) {
			// limitar cantidad de datos tomados para mostrar en el calendario, se priorizan algunos
			if (c > 1)
				break;
			if (column == getTitleFirstIdx())
				continue;
			Object value = table.getValueAt(row, column);
			if (value instanceof BigDecimal && Is.empty(value))
				continue;
			if (value instanceof byte[])
				continue;
			if (result.length() > 0)
				result.append("-");
			result.append(tab.getMetaProperty(column).getLabel());
			result.append(": ");
			//result.append("<b>");
			result.append(format(column, value));
			//result.append("</b>");
			c++;
		}
		return result.toString();
	}
	
	// obtener lo que tenga que estar primero, sino uso el primero por defecto
	private String obtainFirstContentForTitle(int row) {
		String result = "";
		int idx = 0;
		//este if se usa si se busca mas de una propiedad
		//de fabrica es -1
		if (titleFirstIdx < 0) {
		titleFirstIdx = indexForAny(1, "name", "nombre", "title", "titulo", "description", "descripcion", "number",
				"numero", "id");
		//si no encuentra ninguna propiedad anterior, agarra el primero 
		//no se esta contemplando la tabla vacia aun
		idx = (titleFirstIdx < 0 && !tab.getMetaProperties().isEmpty()) ? 0 : titleFirstIdx;
		}
		//int idx = titleFirstIdx;
		//if (idx >= 0) {
			Object value = table.getValueAt(row, idx);
			if (!(value instanceof BigDecimal) && Is.empty(value))
				return result;
			if (value instanceof byte[])
				return result;
			result = tab.getMetaProperty(idx).getLabel() + ": " + format(idx, value);
		//}
		//System.out.println(t);
		return result;
	}
	
	private String format(int column, Object value) {
		MetaProperty p = tab.getMetaProperty(column);
		if (p.hasValidValues()) {
			System.out.println("primer return");
			return p.getValidValueLabel(value);
		} else if (p.getType().equals(boolean.class) || p.getType().equals(Boolean.class)) {
			System.out.println("segundo return");
			return getBooleanFormatter().format(null, value);
		} else {
			System.out.println("tercer return");
			return WebEditors.format(request, p, value, errors, view.getViewName(), true);
		}
	}

	private BooleanFormatter getBooleanFormatter() {
		if (booleanFormatter == null) {
			booleanFormatter = new BooleanFormatter();
		}
		return booleanFormatter;
	}
	
}
