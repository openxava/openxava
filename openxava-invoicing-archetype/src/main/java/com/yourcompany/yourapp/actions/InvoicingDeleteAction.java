package com.yourcompany.yourapp.actions;
 
import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;
 
public class InvoicingDeleteAction extends ViewBaseAction {

    public void execute() throws Exception {
        if (!getView().getMetaModel().containsMetaProperty("deleted")) {
            executeAction("CRUD.delete"); // We call the standard OpenXava
            return;                       //   action for deleting
        }
        Map<String, Object> values =
            new HashMap<>(); // The values to modify in the entity
        values.put("deleted", true); // We set true to 'deleted' property
        MapFacade.setValues( // Modifies the values of the indicated entity
            getModelName(), // A method from ViewBaseAction
            getView().getKeyValues(), // The key of the entity to modify
            values); // The values to change
        resetDescriptionsCache(); // Clears the caches for combos
        addMessage("object_deleted", getModelName());
        getView().clear();
        getView().setKeyEditable(true);
        getView().setEditable(false); // The view is left as not editable
    }
}