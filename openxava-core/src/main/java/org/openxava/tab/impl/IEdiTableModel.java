package org.openxava.tab.impl;

import javax.swing.table.*;

/**
 * <code>TablaModel</code> editable. <p>
 * Or said of another form an I<b>EdiTable</b>Model.<br>
 *
 * @author  Javier Paniza
 */

public interface IEdiTableModel extends TableModel {

  /** Add row at end. */
  void addRow();
  /** Insert a row in indicated position. */
  void insertRow(int row);
  /** Remove the row from indicated position. */
  void removeRow(int row);
  /** Remove rows from indicated positions. */
  void removeRows(int [] filas);
  
}
