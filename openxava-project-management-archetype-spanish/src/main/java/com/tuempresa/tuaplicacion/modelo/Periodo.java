package com.tuempresa.tuaplicacion.modelo;

import java.time.*;

import javax.persistence.*;

import lombok.*;

@Entity @Getter @Setter
public class Periodo extends Nombrable {
	
	LocalDate fechaInicio;
	
	LocalDate fechaFin;
	
}