package com.tuempresa.tuaplicacion.acciones;

import java.util.*;

import javax.ejb.*;

import org.openxava.actions.*;
import org.openxava.model.*;

import com.tuempresa.tuaplicacion.modelo.*;

public class CrearFacturaDesdePedidosSeleccionados
    extends TabBaseAction { // Tipico de acciones de lista. Permite usar getTab() 

    public void execute() throws Exception {
        Collection<Pedido> pedidos = getPedidosSeleccionados(); 
        Factura factura = Factura.crearDesdePedidos(pedidos); 
        addMessage("factura_creada_desde_pedidos", factura, pedidos); 
        
        showDialog(); 
        // A partir de ahora getView() es el diálogo
        getView().setModel(factura); // Visualiza la factura en el diálogo 
        getView().setKeyEditable(false); // Para indicar que el objeto ya existe 
        setControllers("EdicionFactura"); // Las acciones del diálogo 
        
    }

    private Collection<Pedido> getPedidosSeleccionados() 
        throws FinderException
    {
        Collection<Pedido> pedidos = new ArrayList<>();
        for (Map key: getTab().getSelectedKeys()) { 
            Pedido pedido = (Pedido) MapFacade.findEntity("Pedido", key); 
            pedidos.add(pedido);
        }
        return pedidos;
    }
}