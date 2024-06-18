package com.tuempresa.tuaplicacion.calculadores;

import org.openxava.calculators.*;

import com.tuempresa.tuaplicacion.modelo.*;

public class CalculadorEstadoIncidenciaPorDefecto implements ICalculator {

	public Object calculate() throws Exception {
		EstadoIncidencia laDePorDefecto = EstadoIncidencia.findLaDePorDefecto();
		return laDePorDefecto == null?null:laDePorDefecto.getId();
	}
	
}
