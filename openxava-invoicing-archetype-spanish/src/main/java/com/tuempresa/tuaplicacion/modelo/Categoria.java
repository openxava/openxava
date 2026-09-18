package com.tuempresa.tuaplicacion.modelo;
 
import jakarta.persistence.*;

import lombok.*;
 
@Entity @Getter @Setter
public class Categoria extends Identificable{

    @Column(length=50)
    String descripcion;
 
}