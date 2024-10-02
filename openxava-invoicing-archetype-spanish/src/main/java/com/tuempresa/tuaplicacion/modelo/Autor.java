package com.tuempresa.tuaplicacion.modelo;
 
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;
 
@Entity @Getter @Setter
public class Autor extends Identificable{
 
    @Column(length=50) @Required
    String nombre;
    
    @OneToMany(mappedBy="autor")
    @ListProperties("numero, descripcion, precio")
    Collection<Producto> productos;
  
}