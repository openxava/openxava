package org.openxava.tab.impl;

import java.util.*;

import javax.swing.table.*;

/**
 * <code>TableModel</code> that allows put and get all data in <code>Vector</code>
 * format. <p>
 *
 * The data type of each position depends on the concrete<code>TableModel</code>,
 * but typically is a object array (one by column). <br>
 * The vector is assigned vy reference (copy is not made).
 * 
 * @author  Javier Paniza
 */

public interface IVectorTableModel extends TableModel {

  /**
   * Vector with data contained in model. <br>
   *
   * @return <code>[!= null]</code>
   */
  Vector getVector();
  
  /**
   * Set vector with model data. <br>
   *
   * @param vector  If <code>null</code> empty vector is assumed.
   */
  void setVector(Vector vector);
  
}
