package org.openxava.actions;

/**
 *
 * @author Vadim Yezhakov
 */
public class DisableCustomizeListAction extends TabBaseAction {

   public void execute() throws Exception {
       getTab().setCustomizeAllowed(false);
   }

}