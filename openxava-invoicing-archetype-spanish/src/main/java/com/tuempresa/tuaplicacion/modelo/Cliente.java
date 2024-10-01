package com.tuempresa.tuaplicacion.modelo;
 
import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;
 
@Entity  // Esto marca la clase Cliente como una entidad
@Getter @Setter // Esto hace los campos a continuación públicamente accesibles
@View(name="Simple", // Esta vista solo se usará cuando se especifique "Simple"
members="numero, nombre" // Muestra únicamente numero y nombre en la misma lí­nea
)
public class Cliente {
 
    @Id  // La propiedad numero es la clave.  Las claves son obligatorias (required) por defecto
    @Column(length=6)  // La longitud de columna se usa a nivel UI y a nivel DB
    int numero;
 
    @Column(length=50) // La longitud de columna se usa a nivel UI y a nivel DB
    @Required  // Se mostraá un error de validación si la propiedad nombre se deja en blanco
    String nombre;
    
    @Embedded // Así para referenciar a una clase incrustable
    Direccion direccion; // Una referencia Java convencional
 
}