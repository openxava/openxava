package com.tuempresa.tuaplicacion.acciones; // En el paquete 'acciones'

import org.openxava.actions.*; // Necesario para usar OnChangePropertyAction,

public class MostrarOcultarCrearFactura
    extends OnChangePropertyBaseAction { // Necesario para las acciones @OnChange 

    public void execute() throws Exception {
        if (estaPedidoCreado() && estaEntregado() && !tieneFactura()) { 
            addActions("Pedido.crearFactura");
        }
        else {
            removeActions("Pedido.crearFactura");
        }
    }
	
    private boolean estaPedidoCreado() {
        return getView().getValue("oid") != null; // Leemos el valor de la vista
    }
	
    private boolean estaEntregado() {
        Boolean entregado = (Boolean)
            getView().getValue("entregado"); // Leemos el valor de la vista
        return entregado == null?false:entregado;
    }

    private boolean tieneFactura() {
        return getView().getValue("factura.oid") != null; // Leemos el valor de la vista
    } 	
}