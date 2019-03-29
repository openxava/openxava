package org.openxava.tab.impl;

import javax.swing.table.*;

/**
 * A <code>TablaModel</code> that can refresh data. <p>
 *
 * @author  Javier Paniza
 */

 
public interface IRefreshTableModel extends TableModel {

  /**
   * Refresh data from datasource. <br>
   */
  void refresh() throws TabException;
  
}
