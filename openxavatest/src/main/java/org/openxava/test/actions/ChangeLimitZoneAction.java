package org.openxava.test.actions;

import javax.inject.*;

import org.openxava.actions.*;

/**
 * @author Javier Paniza
 */
public class ChangeLimitZoneAction extends ViewBaseAction {
	
	private int newLimitZone;
	
	@Inject
	private int limitZone;

	public void execute() throws Exception {
		setLimitZone(getNewLimitZone());
		resetDescriptionsCache();
	}

	public int getLimitZone() {
		return limitZone;
	}
	public void setLimitZone(int defaultZone) {
		this.limitZone = defaultZone;
	}
	public int getNewLimitZone() {
		return newLimitZone;
	}
	public void setNewLimitZone(int newDefaultZone) {
		this.newLimitZone = newDefaultZone;
	}
}
