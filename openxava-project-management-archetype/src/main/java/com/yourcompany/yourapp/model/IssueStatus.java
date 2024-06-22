package com.yourcompany.yourapp.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;

import lombok.*;

@Entity @Getter @Setter
public class IssueStatus extends Iconable {
			
	boolean useAsDefaultValue;
	
	public static IssueStatus findTheDefaultOne() {
		List<IssueStatus> status = XPersistence.getManager()
			.createQuery("from IssueStatus where useAsDefaultValue = true")
			.getResultList();
		if (status.size() == 1) return status.get(0);
		return null;
	}
		
	private void unsetUseAsDefaultValueForAll() {
		XPersistence.getManager().createQuery("update IssueStatus set useAsDefaultValue = false").executeUpdate();
	}

	public void setUseAsDefaultValue(boolean useAsDefaultValue) {
		if (this.useAsDefaultValue == useAsDefaultValue) return;
		unsetUseAsDefaultValueForAll();
		this.useAsDefaultValue = useAsDefaultValue;
	}

}
