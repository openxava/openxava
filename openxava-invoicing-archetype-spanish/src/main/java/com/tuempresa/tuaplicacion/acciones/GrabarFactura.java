package com.tuempresa.tuaplicacion.acciones;

import org.openxava.actions.*;

public class GrabarFactura
    extends SaveAction { // Acción estándar de OpenXava para 
                         // grabar el contenido de la vista	             
    public void execute() throws Exception {
        super.execute(); // La lógica estándar de grabación 
        closeDialog(); 
    }
}