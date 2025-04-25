package com.yourcompany.yourapp.actions;

import java.time.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.actions.*;
import org.openxava.jpa.*;
import org.openxava.util.*;

import com.yourcompany.yourapp.model.*;

public class NewIssueFromMyCalendarAction extends NewAction {

	private boolean goList = false;
	
	public void execute() throws Exception {
		if ("true".equals(getRequest().getParameter("firstRequest"))) {
			goList = true;
			return;
		}
		super.execute();
		calculatePlanDefaultValue();	
		calculateStatusDefaultValue();
		calculateTypeDefaultValue();
	}

	public String getNextMode() {
		return goList?IChangeModeAction.LIST:IChangeModeAction.DETAIL;
	}

	private void calculatePlanDefaultValue() {
		LocalDate plannedFor = (LocalDate) getView().getValue("plannedFor"); 		
		if (plannedFor == null) {
			plannedFor = LocalDate.now();
			getView().setValue("plannedFor", plannedFor);
		}
		Query query = XPersistence.getManager().createQuery(
			"from Plan p where p.worker.userName = :userName and :plannedFor between p.period.startDate and p.period.endDate");
		query.setParameter("plannedFor", plannedFor);
		query.setParameter("userName", Users.getCurrent());
		List<Plan> plans = query.getResultList(); 
		if (plans.isEmpty()) {
			addError("no_plan_for_user_date", "assignedTo", plannedFor, "'" + Users.getCurrent() + "'"); 
			return;
		}
		Plan plan = plans.get(0);
		getView().setValue("assignedTo.id", plan.getId());
	}
	
	private void calculateStatusDefaultValue() {
		IssueStatus issueStatus = IssueStatus.findTheDefaultOneForMyCalendar();
		if (issueStatus != null) {
			getView().setValue("status.id", issueStatus.getId());
		}
	}
	
	private void calculateTypeDefaultValue() {
		IssueType issueType = IssueType.findTheDefaultOneForMyCalendar();
		if (issueType != null) {
			getView().setValue("type.id", issueType.getId());
		}
	}	
	
}
