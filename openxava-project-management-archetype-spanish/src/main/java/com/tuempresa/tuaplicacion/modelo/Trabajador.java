package com.tuempresa.tuaplicacion.modelo;

import javax.persistence.*;
import javax.validation.constraints.Email;

import org.openxava.jpa.*;

import lombok.*;

@Entity @Getter @Setter
public class Trabajador extends Nombrable { 

	/* Podría ser así con XavaPro
	@DescriptionsList
	@ManyToOne(fetch = FetchType.LAZY)
	User user;  
	*/
	
	@Column(length=30)
	String nombreUsuario; 
	
	@Column(length=60) @Email 
	String correoElectronico;	
	
	public static Trabajador findById(String id) {
		return XPersistence.getManager().find(Trabajador.class, id);
	}
	
}
