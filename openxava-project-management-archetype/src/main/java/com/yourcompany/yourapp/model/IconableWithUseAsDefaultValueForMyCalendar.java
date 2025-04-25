package com.yourcompany.yourapp.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;

import lombok.*;

@MappedSuperclass @Getter @Setter
public class IconableWithUseAsDefaultValueForMyCalendar extends Iconable {
			
	boolean useAsDefaultValueForMyCalendar; 
			
	protected static Object findTheDefaultOne(String entity, String property) {
		List status = XPersistence.getManager()
			.createQuery("from " + entity +  " where " + property + " = true")
			.getResultList();
		if (status.size() == 1) return status.get(0);
		return null;
	}
			
	protected void unsetUseAsDefaultValueForAll(String property) {
		XPersistence.getManager().createQuery("update " + getClass().getSimpleName() + " set " + property + " = false").executeUpdate();
	}
	
	public void setUseAsDefaultValueForMyCalendar(boolean useAsDefaultValueForMyCalendar) {
		if (this.useAsDefaultValueForMyCalendar == useAsDefaultValueForMyCalendar) return;
		unsetUseAsDefaultValueForAll("useAsDefaultValueForMyCalendar");
		this.useAsDefaultValueForMyCalendar = useAsDefaultValueForMyCalendar;
	}

}
