package com.tuempresa.tuaplicacion.modelo;

import java.time.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;

import lombok.*;

@Embeddable @Getter @Setter
public class Actividad {
		
	@Required
	@Column(length=120)
	String descripcion;
	
	@Required
	@DefaultValueCalculator(CurrentLocalDateCalculator.class)
	LocalDate fecha;

}
