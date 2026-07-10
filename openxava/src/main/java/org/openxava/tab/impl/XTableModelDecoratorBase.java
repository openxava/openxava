package org.openxava.tab.impl;

import java.util.*;

import org.openxava.model.*;
import javax.swing.event.*;

import org.openxava.util.*;


/**
 * Base class to create {@link IXTableModel} decorators. <p>
 * 
 * A decorator as describe in GoF book.
 * By default simply redirect the call to <tt>IXTableModel</tt> original. <br>
 *
 * @author  Javier Paniza
 */

public class XTableModelDecoratorBase
	implements IXTableModel, java.io.Serializable {

	private IXTableModel impl;
	
	

	/**
	 * @param toDecorate <tt>IXTableModel</tt> to decorate.
	 * @exception IllegalArgumentException If <tt>toDecorate == null</tt>.
	 */
	public XTableModelDecoratorBase(IXTableModel toDecorate) {
		Assert.arg(toDecorate);
		impl = toDecorate;
	}
	
	public void addTableModelListener(TableModelListener l) {
		impl.addTableModelListener(l);
	}
	
	public Class getColumnClass(int columnIndex) {
		return impl.getColumnClass(columnIndex);
	}
	
	public int getColumnCount() {
		return impl.getColumnCount();
	}
	
	public String getColumnName(int columnIndex) {
		return impl.getColumnName(columnIndex);
	}
	
	public Object getObjectAt(int rowIndex) throws FinderException {
		return impl.getObjectAt(rowIndex);
	}
	
	public int getRowCount() {
		return impl.getRowCount();
	}
	
	public Object getValueAt(int rowIndex, int columnIndex) {
		return impl.getValueAt(rowIndex, columnIndex);
	}
	
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return impl.isCellEditable(rowIndex, columnIndex);
	}
	
	public void refresh() throws TabException {
		impl.refresh();
	}
	
	public void removeAllRows() {
		impl.removeAllRows();
	}
	
	public void removeTableModelListener(TableModelListener l) {
		impl.removeTableModelListener(l);
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		impl.setValueAt(aValue, rowIndex, columnIndex);
	}

	/**
	* @throws SystemException
	 */
	public int getTotalSize() {
		return impl.getTotalSize();		
	}

	/**
	* @throws SystemException
	 */
	public Number getSum(String property) { 
		return impl.getSum(property);		
	}
		
	public void removeRow(Map keyValues) throws FinderException { 
		impl.removeRow(keyValues);	
	}

	/** @since 5.7 */
	public int getChunkSize() { 
		return impl.getChunkSize();
	}

	/** @since 5.7 */
	public boolean isAllLoaded() { 
		return impl.isAllLoaded();
	}
	
}
