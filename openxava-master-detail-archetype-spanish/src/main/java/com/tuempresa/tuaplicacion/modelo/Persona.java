package com.tuempresa.tuaplicacion.modelo;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

// RENAME THIS CLASS AS Customer, Client, Worker, Owner, Seller, etc.

// YOU CAN RENAME THE MEMBERS BELOW AT YOUR CONVENIENCE, 
// FOR EXAMPLE country BY state,
// BUT CHANGE ALL REFERENCES IN ALL CODE USING SEARCH AND REPLACE FOR THE PROJECT. 
// DON'T USE REFACTOR > RENAME FOR MEMBERS BECAUSE IT DOESN'T CHANGE THE ANNOTATIONS CONTENT.

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
