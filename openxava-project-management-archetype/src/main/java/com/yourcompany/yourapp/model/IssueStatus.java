package com.yourcompany.yourapp.model;

import javax.persistence.*;

import org.openxava.jpa.*;

import lombok.*;

@Entity @Getter @Setter
public class IssueStatus extends IconableWithUseAsDefaultValueForMyCalendar {
			
	boolean useAsDefaultValue;
	
	public static IssueStatus findById(String id) { 
		return (IssueStatus) XPersistence.getManager().find(IssueStatus.class, id);
	}
	
	public static IssueStatus findTheDefaultOne() {
		return (IssueStatus) findTheDefaultOne("IssueStatus", "useAsDefaultValue");
	}
		
	public static IssueStatus findTheDefaultOneForMyCalendar() {
		return (IssueStatus) findTheDefaultOne("IssueStatus", "useAsDefaultValueForMyCalendar");
	}	

	public void setUseAsDefaultValue(boolean useAsDefaultValue) {
		if (this.useAsDefaultValue == useAsDefaultValue) return;
		unsetUseAsDefaultValueForAll("useAsDefaultValue");
		this.useAsDefaultValue = useAsDefaultValue;
	}		
	
}
