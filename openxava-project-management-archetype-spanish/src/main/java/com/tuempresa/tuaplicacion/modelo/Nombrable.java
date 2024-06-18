package com.tuempresa.tuaplicacion.modelo;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

@MappedSuperclass @Getter @Setter
public class Nombrable extends Identifiable {

	@Column(length=40) @Required
	String nombre;
	
}
