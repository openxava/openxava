package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza 
 */

public class CorporationEmployeeListFilterAction extends TabBaseAction {
	
	private String segment;

	public void execute() throws Exception {		
		if ("low".equals(segment)) {
			System.out.println("LOW"); 
			getTab().setBaseCondition("e.salary <= 2500");
		}
		else if ("high".equals(segment)) {
			System.out.println("HIGH"); 
			getTab().setBaseCondition("e.salary > 2500");
		}
		else {
			System.out.println("DEFAULT");
			getTab().setBaseCondition("");
		}				
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

}
