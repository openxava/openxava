package com.tuempresa.tuaplicacion.modelo;
 
import jakarta.persistence.*;

import org.openxava.annotations.*;

import lombok.*;
 
@MappedSuperclass @Getter @Setter
public class Eliminable extends Identificable {
 
    @Hidden
    @Column(columnDefinition="BOOLEAN DEFAULT FALSE")
    boolean eliminado;
 
}