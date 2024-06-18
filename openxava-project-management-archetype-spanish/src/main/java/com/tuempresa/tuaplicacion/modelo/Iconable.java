package com.tuempresa.tuaplicacion.modelo;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

@MappedSuperclass @Getter @Setter
public class Iconable extends Nombrable {

	@Column(length=40) @Icon
	private String icono;
	
}
