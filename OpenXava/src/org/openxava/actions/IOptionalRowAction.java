package org.openxava.actions;

import org.openxava.actions.IAction;

/**
 * 
 * @author Subrahmanyam Mahaswami
 */
public interface IOptionalRowAction extends IAction {

    public boolean isApplicableForRow(Object object);

}
