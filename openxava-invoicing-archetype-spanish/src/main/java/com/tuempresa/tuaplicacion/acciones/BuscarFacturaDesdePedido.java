package com.tuempresa.tuaplicacion.acciones; // En el paquete 'acciones'

import org.openxava.actions.*; // Para usar ReferenceSearchAction

public class BuscarFacturaDesdePedido
    extends ReferenceSearchAction { // Lógica estándar para buscar una referencia

    public void execute() throws Exception {
        int numeroCliente =
            getView().getValueInt("cliente.numero"); // Lee de la vista el número
                                                  // de cliente del pedido actual
        super.execute(); // Ejecuta la lógica estándar, la cual muestra un diálogo
        if (numeroCliente > 0) { // Si hay cliente los usamos para filtrar
            getTab().setBaseCondition("${cliente.numero} = " + numeroCliente);
        }
    }
}