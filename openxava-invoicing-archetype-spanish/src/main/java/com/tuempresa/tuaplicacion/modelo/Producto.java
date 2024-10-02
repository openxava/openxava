package com.tuempresa.tuaplicacion.modelo;
 
import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.tuempresa.tuaplicacion.anotaciones.*;

import lombok.*;
 
@Entity @Getter @Setter
public class Producto {
 
    @Id @Column(length=9)
    int numero;
    
    @Column(length=13) 
    @ISBN(buscar=false)
    String isbn;
 
    @Column(length=50) @Required
    String descripcion;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @DescriptionsList
    Autor autor;
    
    @ManyToOne( // La referencia se almacena como una relación en la base de datos
            fetch=FetchType.LAZY, // La referencia se carga bajo demanda
            optional=true) // La referencia puede estar sin valor
    @DescriptionsList // Así la referencia se visualiza usando un combo
    Categoria categoria; // Una referencia Java convencional
    
    @Money // La propiedad precio se usa para almacenar dinero
    BigDecimal precio; // BigDecimal se suele usar para dinero
     
    @Files// Una galería de fotos completa está disponible
    @Column(length=32) // La cadena de 32 de longitud es para almacenar la clave de la galería
    String fotos;
     
    @TextArea // Esto es para un texto grande, se usará un área de texto o equivalente
    String observaciones;
    
}