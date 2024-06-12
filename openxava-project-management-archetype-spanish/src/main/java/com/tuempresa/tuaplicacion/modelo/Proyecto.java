package com.tuempresa.tuaplicacion.modelo;

import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;

import lombok.*;

@Entity @Getter @Setter
public class Proyecto extends Nombrable {
	
	public static Proyecto findUnico() {
		List<Proyecto> projects = XPersistence.getManager().createQuery("from Project").getResultList();
		if (projects.size() == 1) return projects.get(0);
		return null;
	}
	
	public static Proyecto findByNombre(String nombre) { 
		return (Proyecto) XPersistence.getManager()
			.createQuery("from Proyecto p where p.nombre = :nombre")
			.setParameter("nombre", nombre)
			.getSingleResult();
	}
	
}
