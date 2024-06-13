package com.tuempresa.tuaplicacion.modelo;

import java.math.*;
import java.time.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.model.*;
import org.openxava.web.editors.*;

import com.tuempresa.tuaplicacion.calculadores.*;

import lombok.*;

@Entity @Getter @Setter
@View(members=
	"titulo, tipo;" +
	"descripcion;" +
	"detalles [#" +
		"proyecto, creadoPor;" +
		"creadoEl, prioridad;" +
		"version, asignadoA;" +
		"estado, cliente;" +
		"minutos, horas;" +
	"];" +
	"adjuntos;" +
	"discusion"
)
public class Incidencia extends Identifiable {

	@Column(length=100) @Required
	String titulo;
	
	@DescriptionsList
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	TipoIncidencia tipo;
		
	@HtmlText(simple = true) 
	// @Column(columnDefinition="MEDIUMTEXT") // MySQL
	@Column(columnDefinition="LONGVARCHAR") // HSQLDB
	String descripcion;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=true)
	@DescriptionsList
	@DefaultValueCalculator(CalculadorProyectoPorDefecto.class)
	Proyecto proyecto; 
	
	@Column(length=30) @ReadOnly
	@DefaultValueCalculator(CurrentUserCalculator.class)
	String creadoPor;
	
	@ReadOnly 
	@DefaultValueCalculator(CurrentLocalDateCalculator.class) 
	LocalDate creadoEl;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=true)
	@DescriptionsList(order="${level} desc")
	@DefaultValueCalculator(value=IntegerCalculator.class, 
		properties = @PropertyValue(name="value", value="5") )
	Prioridad prioridad; 
		
	@DescriptionsList(condition="proyecto.id = ?", depends="this.proyecto", order="${name} desc") 
	@ManyToOne(fetch=FetchType.LAZY, optional=true)
	Version version;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=true)
	@DescriptionsList(descriptionProperties="worker.name, period.name")
	Plan asignadoA;
	
	@DescriptionsList
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	@DefaultValueCalculator(CalculadorEstadoIncidenciaPorDefecto.class) 
	EstadoIncidencia estado; 
	
	@DescriptionsList
	@ManyToOne(fetch=FetchType.LAZY)
	Cliente cliente; 

	@Max(99999)
	int minutos; 
	
	@ReadOnly
	@Calculation("minutes / 60")
	@Column(length=6, scale=2)
	BigDecimal horas; 
	
	@Files @Column(length=32)
	String adjuntos;
	
	@Discussion
	@Column(length=32)
	private String discusion;
	
	@PreRemove
	void eliminarDiscusion() {
	    DiscussionComment.removeForDiscussion(discusion);
	}

}
