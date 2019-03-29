package org.openxava.web.editors;

import java.math.*;
import java.util.*;

import javax.servlet.http.*;
import javax.swing.table.*;

import org.openxava.formatters.*;
import org.openxava.model.meta.*;
import org.openxava.tab.*;
import org.openxava.util.*;
import org.openxava.view.*;
import org.openxava.web.*;

/**
 * @since 5.7 
 * @author Javier Paniza 
 */
public class CardIterator implements Iterator<Card>, Iterable<Card> {
	
	private View view;
	private transient HttpServletRequest request; 
	private Messages errors;
	private Tab tab;
	private TableModel table; 
	private int row = 0;
	private Collection<String> propertiesNames;
	private int headerColumn = -1;
	private int subheaderColumn = -1;
	private BooleanFormatter booleanFormatter; 
	
	public CardIterator(Tab tab, View view, HttpServletRequest request, Messages errors) {
		this.tab = tab;
		this.table = tab.getTableModel();
		this.view = view;
		this.request = request;
		this.errors = errors;
	}

	public boolean hasNext() {
		return row < table.getRowCount();
	}

	public Card next() {
		Card el = new Card();
		el.setHeader(obtainHeader(row)); 
		el.setSubheader(obtainSubheader(row)); 
		el.setContent(obtainContent(row)); 
		row++;
		return el;
	}

	private String obtainHeader(int row) {		
		return format(getHeaderColumn(), table.getValueAt(row, getHeaderColumn()));
	}
	
	private String obtainSubheader(int row) {
		if (getSubheaderColumn() < 0) return "";
		return format(getSubheaderColumn(), table.getValueAt(row, getSubheaderColumn()));
	}	
	
	private String obtainContent(int row) {
		StringBuffer result = new StringBuffer();
		for (int column=0; column < table.getColumnCount(); column++) {
			if (column == getHeaderColumn() || column == getSubheaderColumn()) continue;
			Object value = table.getValueAt(row, column);
			if (!(value instanceof BigDecimal) && Is.empty(value)) continue; 
			if (value instanceof byte[]) continue;
			if (result.length() > 0) result.append(", ");
			result.append(tab.getMetaProperty(column).getLabel());
			result.append(": ");
			result.append("<b>");
			result.append(format(column, value));
			result.append("</b>");
		}
		return result.toString();
	}
	
	private String format(int column, Object value) {
		MetaProperty p = tab.getMetaProperty(column);
		if (p.hasValidValues()) {
			return p.getValidValueLabel(value);
		}
		else if (p.getType().equals(boolean.class) || p.getType().equals(Boolean.class)) {
			return getBooleanFormatter().format(null, value);
		}
		else {
			return WebEditors.format(request, p, value, errors, view.getViewName(), true);
		}
	}
	
	public BooleanFormatter getBooleanFormatter() {
		if (booleanFormatter == null) {
			booleanFormatter = new BooleanFormatter();
		}
		return booleanFormatter;
	}

	private int getHeaderColumn() {
		if (headerColumn < 0) {
			headerColumn = firstIndexForAny("name", "nombre", "title", "titulo", "description", "descripcion", "number", "numero", "id");
			if (headerColumn < 0 && !tab.getMetaProperties().isEmpty()) headerColumn = 0;
		}
		return headerColumn ;
	}
	
	private int getSubheaderColumn() {
		if (subheaderColumn < 0) {
			subheaderColumn = secondIndexForAny("name", "nombre", "title", "titulo", "description", "descripcion", "number", "numero", "id");
			if (subheaderColumn < 0 && tab.getMetaProperties().size() > 1) {
				if (getHeaderColumn() == 0) subheaderColumn = 1;
				else subheaderColumn = 0;
			}
		}
		return subheaderColumn;
	}
	
	private int firstIndexForAny(String ... properties) {
		return indexForAny(1, properties);
	}
	
	private int secondIndexForAny(String ... properties) {
		return indexForAny(2, properties);
	}

	private int indexForAny(int ordinal, String ... properties) {
		int timesFound = 0;
		for (String propertyName: properties) {
			int idx = 0;
			for (MetaProperty metaProperty: tab.getMetaProperties()) {
				if (metaProperty.getQualifiedName().equals(propertyName)) {
					if (++timesFound == ordinal) {
						return idx;
					}
				}
				idx++;
			}
		}
		return -1;
	}

	public void remove() {
		throw new UnsupportedOperationException(); 
	}

	public Iterator<Card> iterator() {
		return this;
	}
		
}
