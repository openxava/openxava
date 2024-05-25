package com.tuempresa.tuaplicacion.modelo;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

// RENOMBRA ESTA CLASE COMO Cliente, Trabajador, Operario, Propietario, Vendedor, etc.

// PUEDES RENOMBRAR LOS MIEMBROS ABAJO A TU CONVENIENCIA, 
// POR EJEMPLO pais POR provincia, 
// PERO CAMBIA TODAS LAS REFERENCIAS EN EL CÓDIGO USANDO BUSCAR Y REEMPLAZAR PARA EL PROYECTO. 
// NO USES REFACTOR > RENAME PARA LOS MIEMBROS PORQUE NO CAMBIA EL CONTENIDO EN LAS ANOTACIONES.

@Entity @Getter @Setter
@View(members="datos [ numero; nombre; foto; direccion; ciudad; pais ], ubicacion")
@View(name="Simple", members="numero, nombre") 
@Tab(properties="numero, nombre, foto") 
public class Persona {
	
	@Id
	int numero;
	
	@Column(length=40) 
	String nombre;
	
	@File
	@Column(length=32)
	String foto;
	
	@Column(length=40) 
	String direccion; 	

	@Column(length=40)
	String ciudad; 
	
	@Column(length=40)
	String pais; 
	
	@Coordinates 
	@Column(length=50)
	String ubicacion; 
	
}
