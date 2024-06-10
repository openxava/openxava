package com.yourcompany.yourapp.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;

import lombok.*;

@Entity @Getter @Setter
public class Project extends Nameable {
	
	public static Project findUnique() {
		List<Project> projects = XPersistence.getManager().createQuery("from Project").getResultList();
		if (projects.size() == 1) return projects.get(0);
		return null;
	}
	
	public static Project findByName(String name) { 
		return (Project) XPersistence.getManager()
			.createQuery("from Project p where p.name = :name")
			.setParameter("name", name)
			.getSingleResult();
	}
	
}
