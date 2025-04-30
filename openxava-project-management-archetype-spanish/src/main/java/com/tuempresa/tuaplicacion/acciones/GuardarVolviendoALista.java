package com.tuempresa.tuaplicacion.acciones;

import org.openxava.actions.*;

/**
 * Acción que guarda la entidad y vuelve al modo lista si no hay errores.
 * Si hay errores, permanece en el modo detalle.
 */
public class GuardarVolviendoALista extends SaveAction {
	
	public String getNextMode() {
		return getErrors().contains()?DETAIL:LIST;
	}

}
