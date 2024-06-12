package com.tuempresa.tuaplicacion.calculadores;

import org.openxava.calculators.*;

import com.tuempresa.tuaplicacion.modelo.*;

public class CalculadorProyectoPorDefecto implements ICalculator {

	public Object calculate() throws Exception {
		Proyecto unico = Proyecto.findUnico();
		return unico == null?null:unico.getId();
	}

}
