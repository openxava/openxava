package com.tuempresa.tuaplicacion.acciones; // En el paquete 'acciones'

import java.util.*;

import org.openxava.actions.*; // Para usar OnChangeSearchAction
import org.openxava.model.*;
import org.openxava.view.*;

import com.tuempresa.tuaplicacion.modelo.*;

public class BuscarAlCambiarFactura  
    extends OnChangeSearchAction { // Lógica estándar para buscar una referencia cuando
                                   // los valores clave cambian en la interfaz de usuario 
    public void execute() throws Exception {
        super.execute(); // Ejecuta la lógica estándar 
        Map clave = getView() // getView() aquí es la de la referencia, no la principal
            .getKeyValuesWithValue();
        if (clave.isEmpty()) return;  // Si la clave está vacía no se ejecuta más lógica
        Factura factura = (Factura) // Buscamos la factura usando la clave tecleada
            MapFacade.findEntity(getView().getModelName(), clave);
        View vistaCliente = getView().getRoot().getSubview("cliente"); 
        int numeroCliente = vistaCliente.getValueInt("numero");
        if (numeroCliente == 0) { // Si no hay cliente lo llenamos 
            vistaCliente.setValue("numero", factura.getCliente().getNumero());
            vistaCliente.refresh();
        } 
        else { // Si ya hay un cliente verificamos que coincida con el cliente de la factura 
            if (numeroCliente != factura.getCliente().getNumero()) {
                addError("cliente_factura_no_coincide", 
                    factura.getCliente().getNumero(), factura, numeroCliente);
                getView().clear();
            }
        }
    }
}	