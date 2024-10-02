package com.tuempresa.tuaplicacion.acciones; // En el paquete 'acciones'

import org.openxava.actions.*; // Para usar GoAddElementsToCollectionAction

public class IrAnyadirPedidosAFactura
    extends GoAddElementsToCollectionAction { // Lógica estándar para ir a la lista que
                                              // permite añadir elementos a la colección
    public void execute() throws Exception {
        super.execute(); // Ejecuta la lógica estándar, la cual muestra un diálogo
        int numeroCliente =
            getPreviousView() // getPreviousView() es la vista principal (estamos en un diálogo)
                .getValueInt("cliente.numero"); // Lee el número de cliente de la
                                                // factura actual de la vista
        getTab().setBaseCondition( // La condición de la lista de pedidos a añadir
            "${cliente.numero} = " + numeroCliente +
            " and ${entregado} = true and ${factura} is null"
        );
    }
    
    public String getNextController() { // Añadimos este método
        return "AnyadirPedidosAFactura"; // El controlador con las acciones disponibles
    }                                    // en la lista de pedidos a añadir
}