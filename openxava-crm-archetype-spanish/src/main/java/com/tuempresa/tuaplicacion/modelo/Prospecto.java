package com.tuempresa.tuaplicacion.modelo;

import java.time.*;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.annotations.Files;
import org.openxava.model.*;
import org.openxava.util.*;

import lombok.*;

@Entity @Getter @Setter
@View(members=
	"nombre, estado, correoElectronico, ultimoToque;" + 
	"descripcion { descripcion }" +
	"observaciones { observaciones }" + 
	"actividades { actividades }" +
	"adjuntos { adjuntos }" 
)
@Tab(
	properties= "nombre, correoElectronico, ultimoToque, estado.descripcion, estado.terminado, descripcion, observaciones",	
	defaultOrder = "${estado.descripcion}"
) 
public class Prospecto extends Identifiable {
	
	@Column(length=40) @Required
	String nombre;
	
	@DescriptionsList
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	EstadoProspecto estado;
	
	@Email
	@Column(length=80) @DisplaySize(40) 
	String correoElectronico;
	
	@ReadOnly
	LocalDate ultimoToque; 
	
	@HtmlText(simple = true) 
	// @Column(columnDefinition="MEDIUMTEXT") // MySQL
	@Column(columnDefinition="LONGVARCHAR") // HSQLDB
	String descripcion;

	@HtmlText(simple = true) 
	// @Column(columnDefinition="MEDIUMTEXT") // MySQL
	@Column(columnDefinition="LONGVARCHAR") // HSQLDB
	String observaciones;
	
	@ElementCollection @OrderBy("fecha")
	Collection<Actividad> actividades;
	
	@Files
	String adjuntos; 

	public void setActividades(Collection<Actividad> actividades) {
		this.actividades = actividades;
		Actividad last = (Actividad) XCollections.last(actividades);
		if (last == null || last.getFecha() == null) return;
		ultimoToque = last.getFecha();
	}

	public void setUltimoToque(LocalDate ultimoToque) {
	}
	
}
