package com.tuempresa.tuaplicacion.modelo;

import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;

import lombok.*;

@Entity @Getter @Setter
public class Proyecto extends Nombrable {
	
	public static Proyecto findUnico() {
		List<Proyecto> proyectos = XPersistence.getManager().createQuery("from Proyecto").getResultList();
		if (proyectos.size() == 1) return proyectos.get(0);
		return null;
	}
	
	public static Proyecto findByNombre(String nombre) { 
		return (Proyecto) XPersistence.getManager()
			.createQuery("from Proyecto p where p.nombre = :nombre")
			.setParameter("nombre", nombre)
			.getSingleResult();
	}
	
}
