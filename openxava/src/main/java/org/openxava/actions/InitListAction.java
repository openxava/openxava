package org.openxava.actions;

import javax.inject.*;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.openxava.tab.*;

/**
 * @author Javier Paniza
 */
public class InitListAction extends TabBaseAction {
	
	private static Log log = LogFactory.getLog(InitListAction.class);
	
	@Inject
	private Tab mainTab;

	public void execute() throws Exception {
		System.out.println("[InitListAction.execute] 11"); // tmp
		setMainTab(getTab());
		executeAction("ListFormat.select");
		// tmr if (getTab().getTableModel().getRowCount() == 0 || getTab().getTableModel().getColumnCount() == 0) {
		if (isListEmpty()) { // tmr
			String newAction = getQualifiedActionIfAvailable("new"); 
			if (newAction != null) {
				executeAction(newAction);
				return;
			}
		}		
		getTab().setNotResetNextTime(true); // To avoid executing the initial select twice
	}
	
	private boolean isListEmpty() {
		try {
			System.out.println("[InitListAction.isListEmpty] 10"); // tmp
			int rowCount = getTab().getTotalSize();
			if (rowCount < 0) {
				XPersistence.rollback();
				return true;
			}
			return rowCount == 0;
		}
		catch (Throwable th) {
			System.out.println("[InitListAction.isListEmpty] Z"); // tmp
			System.out.println("[InitListAction.isListEmpty] th=" + th); // tmp
			return true;
		}
		/* tmr
		catch (Exception ex) {
		// Impossible to obtain total size of tab
		// Impossible to obtain the result size
			System.out.println("[InitListAction.isListEmpty] Z"); // tmp
			System.out.println("[InitListAction.isListEmpty] XPersistence.getManager().getTransaction().getRollbackOnly()> " + XPersistence.getManager().getTransaction().getRollbackOnly()); // tmp
			XPersistence.rollback();
			System.out.println("[InitListAction.isListEmpty] XPersistence.getManager().getTransaction().getRollbackOnly()< " + XPersistence.getManager().getTransaction().getRollbackOnly()); // tmp
			// log.warn("determine_list_size_error", ex);
			System.out.println("[InitListAction.isListEmpty] FALLO"); // tmp
			return true; // We assume true, so the module works in detail mode even if there is database errors, like the table does not exist
		}
		*/
	}

	public Tab getMainTab() {
		return mainTab;
	}
	public void setMainTab(Tab mainTab) {
		this.mainTab = mainTab;
	}
	
}
