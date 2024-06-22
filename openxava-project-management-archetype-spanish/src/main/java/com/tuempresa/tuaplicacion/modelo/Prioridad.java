package com.tuempresa.tuaplicacion.modelo;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;

import lombok.*;

@Entity @Getter @Setter
@Tab(defaultOrder="${nivel} desc")
public class Prioridad {
	
	@Id @Max(9)
	int nivel;
	
	@Column(length=40) @Required
	String descripcion;

}
