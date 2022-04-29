package org.openxava.test.actions;

import java.util.*;

import org.openxava.actions.*;
import org.openxava.test.model.*;
import org.openxava.util.*;

/**
* Create on 7 dic. 2016 (8:56:20)
* @author Ana Andres
*/
public class ViewNamesOrRolesNamesFromTeamMember extends CollectionBaseAction{
	private boolean roles = false;
	
	public void execute() throws Exception {
		Collection<TeamMember> members = getObjects();
		String message = "";
		for (TeamMember member : members){
			if (!Is.empty(message)) message += ", ";
			message += roles ? member.getRole() : member.getPerson().getName();
		}
		addInfo(Is.empty(message) ? "Nobody" : message);
	}

	public boolean isRoles() {
		return roles;
	}

	public void setRoles(boolean roles) {
		this.roles = roles;
	}

}