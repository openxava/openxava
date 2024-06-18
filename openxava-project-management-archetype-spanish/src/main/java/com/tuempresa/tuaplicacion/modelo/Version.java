package com.tuempresa.tuaplicacion.modelo;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.model.*;

import lombok.*;

@Entity @Getter @Setter
@View(members="proyecto, nombre; incidencias")
@Tab(defaultOrder="${nombre} desc") 
public class Version extends Identifiable {
	
	public static List<Version> findByNombre(String nombre) { 
		return XPersistence.getManager()
			.createQuery("from Version v where v.nombre = :nombre")
			.setParameter("nombre", nombre)
			.getResultList();
	}

	@DescriptionsList
	@ManyToOne(fetch=FetchType.LAZY)
	Proyecto proyecto;
	
	@Column(length=20)
	String nombre; 
	
	@ListProperties("estado.icono, titulo, tipo.icono, creadoPor, asignadoA.trabajador.nombre") 
	@OneToMany(mappedBy="version")
	@OrderColumn
	@NewAction("IncidenciasVersion.new")
	List<Incidencia> incidencias;
	
}
