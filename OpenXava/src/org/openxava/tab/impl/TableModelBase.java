package org.openxava.tab.impl;

import java.io.*;

import javax.swing.event.*;
import javax.swing.table.*;





/**
 * The main difference within this class and swing <code>AbstractTableModel</code>
 * is that the listener list is transient. Hence you can use objects of
 * this class for move data via RMI from server to client without listeners.<br>
 *
 * @author Javier Paniza
 */

 

public abstract class TableModelBase implements TableModel, Serializable
{
	transient private EventListenerList listenerList;

	
	
	public void addTableModelListener(TableModelListener l) {
		getListenerList().add(TableModelListener.class, l);
	}
	
	public int findColumn(String columnName) {
		for (int i = 0; i < getColumnCount(); i++) {
			if (columnName.equals(getColumnName(i))) {
				return i;
			}
		}
		return -1;
	}
	public void fireTableCellUpdated(int row, int column) {
		fireTableChanged(new TableModelEvent(this, row, row, column));
	}
	public void fireTableChanged(TableModelEvent e) {
		Object[] listeners = getListenerList().getListenerList();
		for (int i = listeners.length-2; i>=0; i-=2) {
		    if (listeners[i]==TableModelListener.class) {
			((TableModelListener)listeners[i+1]).tableChanged(e);
		    }
		}
	}

	public void fireTableDataChanged() {
		fireTableChanged(new TableModelEvent(this));
	}
	public void fireTableRowsDeleted(int firstRow, int lastRow) {
		fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
							 TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE));
	}
	public void fireTableRowsInserted(int firstRow, int lastRow) {
		fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
							 TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}
	public void fireTableRowsUpdated(int firstRow, int lastRow) {
		fireTableChanged(new TableModelEvent(this, firstRow, lastRow,
							 TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
	}
	public void fireTableStructureChanged() {
		fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
	}
	public Class getColumnClass(int columnIndex) {
		return Object.class;
	}

	public String getColumnName(int column) {
		String result = "";
		for (; column >= 0; column = column / 26 - 1) {
		    result = (char)((char)(column%26)+'A') + result;
		}
		return result;
	}
	
	private EventListenerList getListenerList() {
	  if (listenerList == null) {
		listenerList = new EventListenerList();
	  }
	  return listenerList;
	}
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}
	public void removeTableModelListener(TableModelListener l) {
		getListenerList().remove(TableModelListener.class, l);
	}
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
	}
	
}
