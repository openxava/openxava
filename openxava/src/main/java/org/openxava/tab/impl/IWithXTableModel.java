package org.openxava.tab.impl;

import org.openxava.util.*;

/**
 * Something with a {@link IXTableModel}. <p>
 *
 * @author  Javier Paniza
 */

public interface IWithXTableModel {

  IXTableModel getTable() throws SystemException;
  
}
