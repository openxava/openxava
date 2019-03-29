package org.openxava.actions;

import org.openxava.util.*;

/**
 * @since 5.9
 * @author Javier Paniza
 */
public class SubscribeToEmailNotificationsAction extends BaseAction {
	
	private Boolean subscribe;

	public void execute() throws Exception {
		if (!EmailNotifications.currentUserHasEmail()) {
			addError("email_subscription_fail"); 
			return;
		}
		if (subscribe) {
			EmailNotifications.subscribeCurrentUserToModule(getManager().getModuleName());
			addMessage("email_subscription_success", getManager().getMetaModule().getLabel()); 
		}
		else {
			EmailNotifications.unsubscribeCurrentUserFromModule(getManager().getModuleName());
			addMessage("email_unsubscription", getManager().getMetaModule().getLabel()); 
		}
		getManager().setActionsChanged(true);
	}

	public Boolean getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(Boolean subscribe) {
		this.subscribe = subscribe;
	}

}
