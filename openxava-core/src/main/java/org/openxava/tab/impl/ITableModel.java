package org.openxava.tab.impl;

import javax.swing.table.*;

/**
 * Adds to the swing <code>TableModel</code> the posibility
 * of add, remove and modify rows. <p> 
 * 
 * In the model there are no null rows, but cells with null
 * are allowed.<br>
 * When add, insert or modify rows you can send null, but the
 * implementation will store a empty array or equivalent.<br>
 *
 * @author  Javier Paniza
 */

public interface ITableModel extends TableModel {

  /**
   * Add a row at end. <p>
   *
   * @param rowData Data to add, if <code>null</code> add a row
   *                which datas are <code>null</code>, but row itself
   * 								is not <code>null</code>.
   */
  void addRow(Object[] rowData);
  
  /**
   * Return a array with data of indicated row. <p>
   *
   * @return <code>!= null</code>
   */
  Object [] getRow(int row);
  
  /**
   * Insert the row in the indicated place. <p>   * 
   *
   * @param rowData Data to insert, if <code>null</code> add a row
   *                which datas are <code>null</code>, but row itself
   * 								is not <code>null</code>.
   */
  void insertRow(int row, Object[] rowData);
  
  void removeAllRows();
  void removeRow(int row);
  
  /**
   * Set new values in the row. <p>
   *
   * @param rowData Data to set, if <code>null</code> add a row
   *                which datas are <code>null</code>, but row itself
   * 								is not <code>null</code>.
   */
  void setRow(int row, Object[] rowData);
  
}
