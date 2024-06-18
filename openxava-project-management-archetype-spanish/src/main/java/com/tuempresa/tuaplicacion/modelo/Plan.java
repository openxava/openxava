package com.tuempresa.tuaplicacion.modelo;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

@Entity @Getter @Setter
@View(members="trabajador, periodo; incidencias")
@Tab(defaultOrder="${periodo.nombre} desc, ${trabajador.nombre} asc") 
public class Plan extends Identifiable {
	
	@DescriptionsList
	@ManyToOne(optional=false)
	Trabajador trabajador; 
	
	@DescriptionsList
	@ManyToOne(optional=false)
	Periodo periodo;
	
	@ListProperties("estado.icono, titulo, tipo.icono, proyecto.nombre, version.nombre") 
	@OneToMany(mappedBy="asignadoA")
	@OrderColumn(name="Plan_incidencias_ORDER")
	List<Incidencia> incidencias;

}
