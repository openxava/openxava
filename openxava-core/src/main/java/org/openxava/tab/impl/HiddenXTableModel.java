package org.openxava.tab.impl;

import java.util.*;




/**
 * Decorate an {@link IXTableModel} to hidden columns. <p>
 *
 * It's easy to use, for example:
 * <pre>
 * int [] hidden = {0, 1};
 * IXTableModel decorated = new HiddenXTableModel(original, hidden);
 * </pre>
 * After this you can use <tt>decorated</tt> which display data
 * from <tt>original</tt> but without columns 1 and 2.<br>
 *
 * @author  Javier Paniza
 */
public class HiddenXTableModel extends XTableModelDecoratorBase {

  private int [] indexes;
  private int columnCount;

  /**
   * @param toDecorate  <tt>TableModel</tt> to decorate hidden columns
   * @param hiddenIndexes  Indexes of columns to hidden, if <tt>null</tt>
   *                       no columns are hidden.
   * @exception IllegalArgumentException  If <tt>toDecorate == null</tt>.
   */
  public HiddenXTableModel(IXTableModel toDecorate, int [] hiddenIndexes) {
		super(toDecorate);
		// assert(toDecorate);
		int nc = toDecorate.getColumnCount();
		Vector original = new Vector();
		int i;
		// put the original indexes
		for (i=0; i<nc; i++) {
		  original.add(new Integer(i));
		}
		// remove the hidden ones, if apply
		if (hiddenIndexes != null) {
		  for (i=0; i<hiddenIndexes.length; i++) {
			original.remove(new Integer(hiddenIndexes[i]));
		  }
		}
		// Init columnCount and indexes
		columnCount = original.size();
		indexes = new int[columnCount];
		for (i=0; i<columnCount; i++) {
		  indexes[i] = ((Integer) original.get(i)).intValue();
		}
  }

  public Class getColumnClass(int columnIndex) {
  	return super.getColumnClass(toOriginalColumn(columnIndex));
  }

  public int getColumnCount() {
  	return columnCount;
  }

  public String getColumnName(int columnIndex) {
  	return super.getColumnName(toOriginalColumn(columnIndex));
  }

  public Object getValueAt(int rowIndex, int columnIndex) {
  	return super.getValueAt(rowIndex, toOriginalColumn(columnIndex));
  }

  public boolean isCellEditable(int rowIndex, int columnIndex) {
  	return super.isCellEditable(rowIndex, toOriginalColumn(columnIndex));
  }

  public void setValueAt(Object value, int rowIndex, int columnIndex) {
  	super.setValueAt(value, rowIndex, toOriginalColumn(columnIndex));
  }
  
  private int toOriginalColumn(int visibleColumn) {
  	return indexes[visibleColumn];
  }
    
}
