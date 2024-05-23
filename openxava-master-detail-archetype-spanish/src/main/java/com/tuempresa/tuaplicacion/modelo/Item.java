package com.tuempresa.tuaplicacion.modelo;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;


// RENOMBRA ESTA CLASE COMO Producto, Articulo, Tarea, TipoMovimiento, etc.

// PUEDES RENOMBRAR LOS MIEMBROS ABAJO A TU CONVENIENCIA, 
// POR EJEMPLO pais POR provincia, 
// PERO CAMBIA TODAS LAS REFERENCIAS EN EL CÓDIGO USANDO BUSCAR Y REEMPLAZAR PARA EL PROYECTO. 
// NO USES REFACTOR > RENAME PARA LOS MIEMBROS PORQUE NO CAMBIA EL CONTENIDO EN LAS ANOTACIONES.

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
