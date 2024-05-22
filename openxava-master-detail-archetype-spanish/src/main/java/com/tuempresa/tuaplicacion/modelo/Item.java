package com.tuempresa.tuaplicacion.modelo;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

// RENAME THIS CLASS AS Product, Article, Task, TransactionType, etc.

// YOU CAN RENAME THE MEMBERS BELOW AT YOUR CONVENIENCE, 
// FOR EXAMPLE unitPrice BY hourPrice,
// BUT CHANGE ALL REFERENCES IN ALL CODE USING SEARCH AND REPLACE FOR THE PROJECT. 
// DON'T USE REFACTOR > RENAME FOR MEMBERS BECAUSE IT DOESN'T CHANGE THE ANNOTATIONS CONTENT.

@Entity @Getter @Setter
public class Item {
	
	@Id @Column(length=9)
	int codigo;
	
	@Column(length=40) @Required
	String descripcion;
	
	@Required
	BigDecimal precioUnitario;
	
	@Files
	@Column(length=32)
	String fotos; 

}
