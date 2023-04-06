package org.openxava.web.editors;

import java.math.*;
import java.rmi.*;
import java.time.*;
import java.util.*;

import javax.servlet.http.*;
import javax.swing.table.*;

import org.openxava.controller.*;
import org.openxava.filters.*;
import org.openxava.formatters.*;
import org.openxava.model.meta.*;
import org.openxava.session.*;
import org.openxava.tab.Tab;
import org.openxava.util.*;
import org.openxava.view.View;
import org.openxava.web.*;
import org.openxava.web.dwr.*;

import lombok.*;


@Getter
@Setter
public class CalendarEventIterator {

	View view;
	transient HttpServletRequest request;
	Messages errors;
	// @Inject
	Tab tab;
	TableModel table;
	int row = 0;
	int dateIndex = 0;
	String dateLabel = "";
	String dateName = "";
	int titleFirstIdx = -1;
	String title;
	CalendarEvent event;
	BooleanFormatter booleanFormatter; 
	DateFilter dateFilter = new DateFilter();
	Date currentMonth;
	String date2 = "";
	@Getter @Setter
	String initDate = "";

	List<String> listTitle = new ArrayList<>();
	// List<String> comparators = new ArrayList<>();
	String[] comparators;
	List<String> conditionValues = new ArrayList<>();
	List<String> conditionValuesTo = new ArrayList<>();
	String[] conditionValuesTo2;
	
	boolean primera = false;

	public CalendarEventIterator(Tab tab, View view, HttpServletRequest request, Messages errors) {
		this.tab = tab;
		this.table = tab.getTableModel();
		this.view = view;
		this.request = request;
		this.errors = errors;
		this.initDate = LocalDate.now().toString();
	}

	// Tomar todas las filas de este mes si no hay condiciones
	public List<CalendarEvent> getEvents() throws RemoteException {
		List<CalendarEvent> listEvent = new ArrayList<>();
		// en cada evento debo tener start end y title
		// primero obtengo la columna de la(las) fechas
		// luego hago la iteracion por cada
		
		// System.out.println(tab.getTotalSize());
		// obtengo el index de algun tipo de dato de fecha
		// System.out.println(tab.getMetaProperties().get(2).getTypeName());
		List<MetaProperty> properties = tab.getMetaProperties();
		//dateIndex = indexForAny(1, "java.util.Date");

		// obtengo el label de ese tipo de datos para filtrar y filtro
		//dateLabel = tab.getMetaProperties().get(dateIndex).getLabel();

		// nombre de la propiedad
		//dateName = tab.getMetaProperties().get(dateIndex).getName();

		// settear condicion cuando es nulo
		// comparators = tab.getConditionComparators();
		// System.out.println(tab.getMetaTab().getBaseCondition());
		if (tab.getConditionComparators() != null) {
		} else {
			System.out.println("basecondition");
			System.out.println(tab.getBaseCondition());
			if (tab.getBaseCondition() == null) {
				DateFilter dateFilter = new DateFilter();
		        Calendar calendar = Calendar.getInstance();
		        Date firstDayOfMonth = getFirstDayOfMonth(calendar.getTime());
		        Date lastDayOfMonth = getLastDayOfMonth(calendar.getTime());
				dateFilter.setStart(firstDayOfMonth);
				dateFilter.setEnd(lastDayOfMonth);
//				Labels label = new Labels();
//				label.get("today", locale, "todayD");
				System.out.println(firstDayOfMonth.toString() + "---" + lastDayOfMonth.toString());
				
				tab.setFilter(dateFilter);
				//obtener la propiedad a filtrar y usar primer y ultimo dia del mes
				tab.setBaseCondition("date between ? and ?");
				
				System.out.println("no hay condicion, size: " + tab.getTotalSize());
				//System.out.println(Arrays.toString(tab.getMetaTab().getMetaModel().getMetaProperties().toArray()));
				//System.out.println(Arrays.toString(tab.getMetaProperties().toArray()));
				//createTab();
				//tab.getMetaPropertiesNoCalculated()
				//tab.getcondi
				// llamar a filter() de tab para setConditionParameters
				// getMetaPropertiesNoCalculated
				// tab.setBaseCondition( "${" + dateName + "} between '2002-11-01' and
				// '2004-12-31'");
			} else {
				
			}
			
			
		}
		

		//System.out.println(tab.getPropertiesNamesAsString());
		// System.out.println(tab.getTotalPropertiesNames()); []
		// Queda por determinar que usar entre los 2
		//System.out.println(tab.getTableModel().getTotalSize());
		//System.out.println(tab.getTableModel().getRowCount());

		// si no hay condicion previa, filtro yo mismo en el mes actual para evitar
		// hacer loop entre miles de registros
		if (tab.getConditionValues() != null) {
			System.out.println("getConditionValues");
			System.out.println(Arrays.toString(tab.getConditionValues()));
			System.out.println(Arrays.toString(tab.getConditionValuesTo()));
			System.out.println(Arrays.toString(tab.getConditionComparators()));
		} else {

		}
		
		//si hay filas en la tabla, leer una por una y cargar en objetos event
		
		int tableSize = 0;
		tableSize = tab.getTableModel().getTotalSize();
		if (tableSize > 0) {
			//System.out.println("tabSize");
			
			for (int i = 0; i<tableSize; i++) {
				//System.out.println("for");
				event = new CalendarEvent();
				event.setStart(obtainDateContent(i));
				event.setEnd(date2);
				event.setTitle(obtainContent(i));
				event.setRow(Integer.toString(i));
				listEvent.add(event);
			}
			
		}
		
		//onclick="if (!getSelection().toString()) openxava.executeAction('<%=request.getParameter("application")%>', '<%=request.getParameter("module")%>', false, false, '<%=action%>', '<%="row=" + (i++)%>');"
	    primera = true;
		
		return listEvent;
	}
	
	/*
	public List<CalendarEvent> getEvents(String accion, LocalDate date) throws RemoteException {
		System.out.println("getEvents2");
		return null;
		/*
		List<CalendarEvent> listEvent = new ArrayList<>();
		DateFilter dateFilter = new DateFilter();
        Calendar calendar = Calendar.getInstance();
        Date firstDayOfMonth = getFirstDayOfMonth(calendar.getTime());
        Date lastDayOfMonth = getLastDayOfMonth(calendar.getTime());
		dateFilter.setStart(firstDayOfMonth);
		dateFilter.setEnd(lastDayOfMonth);
		System.out.println(firstDayOfMonth.toString() + "---" + lastDayOfMonth.toString());
		
		
		int tableSize = 0;
		tableSize = tab.getTableModel().getTotalSize();
		if (tableSize > 0) {
			for (int i = 0; i<tableSize; i++) {
				event = new CalendarEvent();
				event.start = obtainDateContent(i);
				event.title = obtainContent(i);
				event.end = date2;
				event.row = Integer.toString(i);
				listEvent.add(event);
			}
		}
		
		return listEvent;
	}*/


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

	// obtener la fecha para start
	private String obtainDateContent(int row) {
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
	
//	private String obtainDate2Content(int row) {
//		String t = "";
//		String[] s;
//		t = obtainDateContent(row);
//		s = t.split(" ");
//		t = s[1].replaceAll("0", "1");
//		return s[0] + " " + t;
//	}

//	private int getFirstIdxForAny() {
//		if (titleFirstIdx < 0) {
//			titleFirstIdx = indexForAny();
//			if (titleFirstIdx < 0 && !tab.getMetaProperties().isEmpty())
//				titleFirstIdx = 0;
//		}
//		return titleFirstIdx;
//	}

//	private int getDateIdx() {
//		int fromInit = -1;
//		fromInit = indexForAny(1, "java.util.Date", "Date", "date");
//		if (fromInit < 0 && !tab.getMetaProperties().isEmpty()) fromInit = 0;
//		return fromInit;
//	}
	
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

	public BooleanFormatter getBooleanFormatter() {
		if (booleanFormatter == null) {
			booleanFormatter = new BooleanFormatter();
		}
		return booleanFormatter;
	}
	
	
	private static void createTab(HttpServletRequest request, Tab tab)  {
		Tab calendarTab = tab.clone();
		calendarTab.clearProperties();
		
		calendarTab.setRequest(request);
		//esto no se usa
		//crear yo una nueva tabla buscando Dates y filtrar por eso
		
		/*
		for (ChartColumn column: chart.getColumns()) {
			addColumn(calendarTab, column);
		}*/
		
			ChartColumn column = new ChartColumn();
			
			
			System.out.println(Arrays.toString(tab.getMetaTab().getMetaModel().getMetaProperties().toArray()));
			System.out.println(Arrays.toString(tab.getMetaProperties().toArray()));
			/*
			
			for (MetaProperty property : tab.getMetaTab().getMetaModel().getMetaProperties()) {
				calendarTab.
			
			}*/
			/*
			MetaProperty property = tab.getMetaTab().getMetaModel().getMetaProperty();
			if (property != null) {
				column.setChart(chart);
				column.setName(chart.getxColumn());				
				addColumn(calendarTab, column);
			}*/
		

		getContext(request).put(request, "xava_calendarTab", calendarTab); 
	}
	
	private static void addColumn(Tab tab, ChartColumn column) {	
		if (!tab.containsProperty(column.getName())) {  
			tab.addProperty(column.getName());			
			tab.setLabel(column.getName(), column.getLabel());
		}
	}

	private static ModuleContext getContext(HttpServletRequest request) { 
		return (ModuleContext) request.getSession().getAttribute("context");
	}
	
	
	private static void filtEvents(String date) {
		if (!date.isEmpty()) {
			//no es vacio
			DateFilter dateFilter = new DateFilter();
	        Calendar calendar = Calendar.getInstance();
	        Date firstDayOfMonth = getFirstDayOfMonth(calendar.getTime());
	        Date lastDayOfMonth = getLastDayOfMonth(calendar.getTime());
			dateFilter.setStart(firstDayOfMonth);
			dateFilter.setEnd(lastDayOfMonth);
		}
		DateFilter dateFilter = new DateFilter();
        Calendar calendar = Calendar.getInstance();
        Date firstDayOfMonth = getFirstDayOfMonth(calendar.getTime());
        Date lastDayOfMonth = getLastDayOfMonth(calendar.getTime());
		dateFilter.setStart(firstDayOfMonth);
		dateFilter.setEnd(lastDayOfMonth);
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
}
