package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class SetFocusOnTypeAction extends ViewBaseAction {
    public void execute() throws Exception {
        getView().setFocus("type");
    }
}
