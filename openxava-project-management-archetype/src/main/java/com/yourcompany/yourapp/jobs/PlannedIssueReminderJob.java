package com.yourcompany.yourapp.jobs;

import java.time.format.*;

import org.apache.commons.logging.*;
import org.openxava.jpa.*;
import org.openxava.util.*;
import org.quartz.*;

import com.yourcompany.yourapp.model.*;

public class PlannedIssueReminderJob implements Job {
	
	private static final Log log = LogFactory.getLog(PlannedIssueReminderJob.class);
    
    public void execute(JobExecutionContext context) {
    	String issueId = "Unknow";
    	String workerEmail = "Unknow";
    	try {
    		issueId = context.getJobDetail().getJobDataMap().getString("issue.id");
			String schema = context.getJobDetail().getJobDataMap().getString("schema");
			if (schema != null) {
				XPersistence.setDefaultSchema(schema);
			}
    		Issue issue = Issue.findById(issueId);
    		if (issue == null) {
    			log.error(XavaResources.getString("planned_issue_reminder_issue_not_found", issueId));
    			return;
    		}
    		Plan plan = issue.getAssignedTo();
    		if (plan == null) {
    			log.error(XavaResources.getString("planned_issue_reminder_no_field", issueId, "plan"));
    			return;
    		}
    		Worker worker = plan.getWorker();
    		if (worker == null) {
    			log.error(XavaResources.getString("planned_issue_reminder_no_field", issueId, "worker"));
    			return;
    		}
    		workerEmail = worker.getEmail();
    		if (Is.emptyString(workerEmail)) {
    			log.error(XavaResources.getString("planned_issue_reminder_no_field", issueId, "email"));
    			return;
    		}
    		if (issue.getPlannedFor() == null) {
    			log.error(XavaResources.getString("planned_issue_reminder_no_field", issueId, "plannedFor"));
    			return;
    		}
    		
    		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
    		String formattedDate = issue.getPlannedFor().format(formatter);
    		
    		String subject = XavaResources.getString("planned_issue_reminder_subject", issue.getTitle(), formattedDate);
    		String content = XavaResources.getString("planned_issue_reminder_content", issue.getTitle(), issue.getDescription(), formattedDate);
    		
    		Emails.send(workerEmail, subject, content);
    	}
    	catch (Exception ex) {
    		log.error(XavaResources.getString("planned_issue_reminder_error", issueId, workerEmail), ex);
    	}
    	finally {
    		XPersistence.commit();
    	}    	
    }
    
}
