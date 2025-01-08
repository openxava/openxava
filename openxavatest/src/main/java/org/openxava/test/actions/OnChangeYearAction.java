package org.openxava.test.actions;

import org.openxava.actions.*;

public class OnChangeYearAction extends OnChangePropertyBaseAction {

    public void execute() throws Exception {
        int year = (Integer) getNewValue();
        if (year == 2000) {
            getView().setHidden("sectionA", false);
        }
        else {
            getView().setHidden("sectionA", true);
        }
    }

}
