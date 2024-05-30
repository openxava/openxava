package com.tuempresa.tuaplicacion.modelo;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

@Entity @Getter @Setter
public class EstadoProspecto extends Identifiable {
	
	@Column(length=40) @Required
	String descripcion;
	
	boolean terminado;

}
