package com.tuempresa.tuaplicacion.acciones;

import org.openxava.actions.*;

public class CrearNuevaIncidenciaEnVersion extends CreateNewElementInCollectionAction {
	
	public void execute() throws Exception {
		String idProyecto = getView().getValueString("proyecto.id"); 
		super.execute();
		getCollectionElementView().setValue("proyecto.id", idProyecto);
		getCollectionElementView().setEditable("proyecto", false);
	}

}
