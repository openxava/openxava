package com.tuempresa.tuaplicacion.calculadores;

import static org.openxava.jpa.XPersistence.getManager;

import org.openxava.calculators.*;

import com.tuempresa.tuaplicacion.modelo.*;

import lombok.*;

public class CalculadorPrecioUnitario implements ICalculator {
	
	@Getter @Setter
	int codigo;  

	public Object calculate() throws Exception {
		return getManager().find(Item.class, codigo).getPrecioUnitario();  
	}

}
