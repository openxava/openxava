package org.openxava.tab.impl;

import java.math.*;
import java.rmi.*;
import java.util.*;

import javax.ejb.*;
import javax.swing.event.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * <code>IXTableModel</code> implementation in JavaBean format. <p>
 *
 * It's a read only <code>TableModel</code>.<br>
 * 
 * Call to {@link #invariant} before use.
 *
 * @author Javier Paniza
 */

public class TableModelBean implements IXTableModel, java.io.Serializable {
	
	private static Log log = LogFactory.getLog(TableModelBean.class);
	
	private final static int STILL_NO_OBTAINED = -1;
	private int totalSize = STILL_NO_OBTAINED;
	private Vector listeners;
	private IEntityTabImpl entityTab;
	private String[] columnsClasses;
	private String[] heading = new String[0];
	private int[] indexesPK = { 0 };
	private List propertiesNames; 
	boolean allLoaded;

	private Vector data;

	// rowCount always has one more that really loaded,
	// thus when the last is try to load the deman more.
	// If all are loaded rowCount is the real total count loaded.
	private int rowCount;
	private boolean translateHeading = true;

	
	
	public TableModelBean() {
		refresh();
	}

	public void addTableModelListener(TableModelListener l) {
		if (listeners == null)
			listeners = new Vector();
		listeners.addElement(l);
	}

	private void fireModelChanged() {
		if (listeners != null) {
			TableModelEvent ev = new TableModelEvent(this);
			Enumeration e = listeners.elements();
			while (e.hasMoreElements()) {
				((TableModelListener) e.nextElement()).tableChanged(ev);
			}
		}
	}
	
	public String[] getHeading() {
		return heading;
	}
	
	public String[] getColumnsClasses() {
		return columnsClasses;
	}
	
	
	public Class getColumnClass(int columnIndex) {
		Class rs = Object.class;
		if (columnsClasses != null) {
			try {
				rs = Class.forName(columnsClasses[columnIndex]);
			}
			catch (ClassNotFoundException ex) {
				log.error(XavaResources.getString("class_not_found_for_column_warning", new Integer(columnIndex)), ex);
			}
			catch (IndexOutOfBoundsException ex) {
			}
		}
		return rs;
	}
		
	public int getColumnCount() {
		return heading.length;
	}
	
	public String getColumnName(int columnIndex) {
		String label = heading[columnIndex];
		if (!isTranslateHeading()) return label;
		int idx = label.indexOf('.');
		if (idx < 0) return label;
		String bundle = label.substring(0, idx);
		String id = label.substring(idx+1);
		try {
			return ResourceBundle.getBundle(bundle).getString(id); 
		}
		catch (MissingResourceException ex) {		
			log.error(XavaResources.getString("resource_not_found_in_bundle", id, bundle),ex);
			return label;
		}
	}
	
	public IEntityTabImpl getEntityTab() {
		return entityTab;
	}
	
	// Warning!, it can return null, for example it's empty
	// Load data on demand if you request a row not loaded yet
	private Object[] getRow(int rowIndex) throws RemoteException {
		if (!allLoaded
			&& rowIndex >= rowCount - 1) { // If you request the last and there are more
			long iniNextChunk = System.currentTimeMillis();
			DataChunk sig = entityTab.nextChunk();
			long timeNextChunk =
				System.currentTimeMillis() - iniNextChunk;
			log.debug("nextChunk=" + timeNextChunk);
			List newData = sig.getData();
			Iterator it = newData.iterator();
			while (it.hasNext()) {
				this.data.addElement(it.next());
			}
			allLoaded = sig.isLast();
			rowCount = allLoaded ? this.data.size() : this.data.size() + 1;
			fireModelChanged();
		}
		if (rowIndex >= rowCount) return null; // if list is empty, for example
		return (Object[]) this.data.elementAt(rowIndex);
	}
	
	/**
	 * Indexes of columns that contains the primary key. <br>
	 * Primary key is used for create the object associated to row.
	 */
	public int[] getPKIndexes() {
		return indexesPK;
	}
	
	public Object getObjectAt(int rowIndex) throws FinderException {
		try {
			Object[] key = new Object[indexesPK.length];
			Object[] row = getRow(rowIndex);			
			if (row == null) {
				return null;
			}
			for (int i = 0; i < key.length; i++) {
				key[i] = row[indexesPK[i]];
			}
			return entityTab.findEntity(key);
		}
		catch (RemoteException ex) {
			throw new FinderException(
					XavaResources.getString("tab_entity_find_error"));
		}
	}
	
	public int getRowCount() {
		return rowCount;
	}
	
	public Object getValueAt(int rowIndex, int columnIndex)
		throws NoSuchElementException {
		try {
			Object[] row = getRow(rowIndex);
			if (row == null)
				return null;
			return convert(row[columnIndex], columnIndex);
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return "ERROR!";
		}
	}
	private Object convert(Object object, int columnIndex) {
		if (object == null) return null;
		if (object.getClass().equals(BigDecimal.class)) {
			if (Integer.class.equals(getColumnClass(columnIndex))) {
				return new Integer(((Number) object).intValue());
			}
			if (Long.class.equals(getColumnClass(columnIndex))) {
				return new Long(((Number) object).longValue());
			}						
		}				
		return object;		 
	}
	
	/**
	 * Verify object invariant. <br>
	 * 
	 * <b>Invariant:</b>
	 * <ul>
	 * <li> headers != null
	 * <li> entityTab != null
	 * <li> indexesPK != null
	 * </ul>
	 * 
	 * @exception IllegalStateException  If invariant is broken
	 */
	public void invariant() throws IllegalStateException {
		if (heading != null && entityTab != null && indexesPK != null)
			throw new IllegalStateException(XavaResources.getString("table_model_bean_invariante"));
	}
	 
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	public void refresh() {		
		totalSize = STILL_NO_OBTAINED;
		this.data = new Vector(50, 50);
		rowCount = 1;
		allLoaded = false;
		try {
			if (entityTab != null) {
				entityTab.reset();
			}
		}
		catch (Exception ex) {			
			log.warn(XavaResources.getString("tab_reset_warning"), ex);
		}
		fireModelChanged();
	}
	
	public void removeAllRows() {
		this.data.clear();
		rowCount = 0;
		fireModelChanged();
	}
	
	public void removeTableModelListener(TableModelListener l) {
		if (listeners != null)
			listeners.removeElement(l);
	}
	
	public void setHeading(String[] heading) {
		this.heading = heading;
	}
	
	public void setColumnsClasses(String[] columnsClasses) {
		this.columnsClasses = columnsClasses;
	}
		
	public void setEntityTab(IEntityTabImpl entityTab) {
		this.entityTab = entityTab;
	}
	
	/**
	 * Indexes of columns that contains the primary key. <br>
	 * Primary key is used for create the object associated to row.
	 */
	public void setPKIndexes(int[] indicesPK) {
		this.indexesPK = indicesPK;
	}
		
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		// do nothing
	}
	
	/** Return <i>TablaModelBean> Rows: x, Columns: x. */
	public String toString() {
		return "TableModelBean> Rows: "
			+ getRowCount()
			+ ", Columns: "
			+ getColumnCount();
	}
	
	public int getTotalSize() throws RemoteException {
		if (totalSize == STILL_NO_OBTAINED) {
			totalSize = entityTab.getResultSize(); 
		}
		return totalSize; 
	}
	
	public Number getSum(String property) throws RemoteException {		
		Number result = entityTab.getSum(property);
		return (Number) convert(result, getPropertiesNames().indexOf(property)); 
	}

	
	/**
	 * If <tt>true</tt> tries to translate the heading. <p> 
	 * 
	 * If a dot is found in heading label assume resourceFile.nombreId.<br>
	 * For example, if the label is <tt>MyAplicationResources.plazo</tt> then
	 * find in <tt>MyAplicationResources</tt> resource bundle the identifier
	 * <tt>plazo</tt>.<br>
	 * If no dot the take the label as is.<br>  
	 * Of course, if this properti is <tt>false</tt> do nothing.<br>
	 * By default is <tt>true</tt>
	 */
	public boolean isTranslateHeading() {
		return translateHeading;
	}

	public void setTranslateHeading(boolean b) {
		translateHeading = b;
	}

	public void setPropertiesNames(List propertiesNames) {
		this.propertiesNames = propertiesNames;
	}

	public List getPropertiesNames() {
		return propertiesNames;
	}

	public void removeRow(Map keyValues) throws FinderException { 		
		for (int i=0; i<data.size(); i++) {
			if (keyValues.equals(getObjectAt(i))) {
				data.remove(i);
				rowCount--;
				if (totalSize != STILL_NO_OBTAINED) totalSize--; 
				return;
			}
		}
	}

	/** @since 5.7 */
	public int getChunkSize() { 
		return entityTab.getChunkSize();
	}

	/** @since 5.7 */
	public boolean isAllLoaded() { 
		return allLoaded;
	}
	
}
