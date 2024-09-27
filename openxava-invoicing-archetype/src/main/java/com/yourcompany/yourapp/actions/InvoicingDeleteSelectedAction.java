package com.yourcompany.yourapp.actions;
 
import java.util.*;

import org.openxava.actions.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;

import lombok.*;
 
public class InvoicingDeleteSelectedAction
    extends TabBaseAction // To work with tabular data (list) by means of getTab()
    implements IChainActionWithArgv { // It chains to another action, returned by getNextAction() method
 
    private String nextAction = null; // To store the next action to execute
 
    public void execute() throws Exception {
        if (!getMetaModel().containsMetaProperty("deleted")) {
            nextAction="CRUD.deleteSelected"; // 'CRUD.deleteSelected' will be
            return;    // executed after this action is finished
        }
        markSelectedEntitiesAsDeleted(); // The logic to mark the selected rows
            // as deleted objects
    }
 
    private MetaModel getMetaModel() {
        return MetaModel.get(getTab().getModelName());
    }
 
    public String getNextAction() // Required because of IChainAction
        throws Exception
    {
        return nextAction; // If null no action will be chained
    }
 
    public String getNextActionArgv() throws Exception {
        return "row=" + getRow(); // Argument to send to chainged action
    }
 
    @Getter @Setter
    boolean restore; // A new restore property
        
    private void markSelectedEntitiesAsDeleted() throws Exception {
        Map<String, Object> values = new HashMap<>(); // Values to assign to each entity to be marked
        //values.put("deleted", true); // Just set deleted to true
        values.put("deleted", !isRestore()); // the restore property value
        Map<String, Object>[] selectedOnes = getSelectedKeys(); // We get the selected rows
        if (selectedOnes != null) {
            for (int i = 0; i < selectedOnes.length; i++) { // Loop over all selected rows
                Map<String, Object> key = selectedOnes[i]; // We obtain the key of each entity
                try {
                    MapFacade.setValues(  // Each entity is modified
                        getTab().getModelName(),
                        key,
                        values);
                }
                catch (javax.validation.ValidationException ex) { // If there is a ValidationException...
                    addError("no_delete_row", i, key);
                    addError("remove_error", getTab().getModelName(), ex.getMessage()); // ...we show the message
                }
                catch (Exception ex) { // If any other exception is thrown, a generic
                    addError("no_delete_row", i, key); // message is added
                }
            }
        }
        getTab().deselectAll(); // After removing we deselect the rows
        resetDescriptionsCache(); // And reset the cache for combos for this user
    }
 
}