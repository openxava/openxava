package org.openxava.test.actions;

import org.openxava.actions.*;

/**
 * 
 * @author Javier Paniza
 */

public class SetMemberEditableOnAction extends ViewBaseAction {

	private String member;
	
	public void execute() throws Exception {
		getView().setEditable(member, true);		
	}
		
	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

}
