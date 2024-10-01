package com.tuempresa.tuaplicacion.acciones; // En el paquete 'acciones'

import java.rmi.*;
import java.util.*;

import javax.ejb.*;

import org.openxava.actions.*; // Para usar AddElementsToCollectionAction
import org.openxava.model.*;
import org.openxava.util.*;
import org.openxava.validators.*;

import com.tuempresa.tuaplicacion.modelo.*;

public class AnyadirPedidosAFactura
    extends AddElementsToCollectionAction { // Lógica estándar para añadir
                                            // elementos a la colección
    public void execute() throws Exception {
        super.execute(); // Usamos la lógica estándar "tal cual"
        getView().refresh(); // Para visualizar datos frescos, incluyendo los importes
    }                        // recalculados, que dependen de las líneas de detalle

    protected void associateEntity(Map clave) // El método llamado para asociar
        throws ValidationException, // cada entidad a la principal, en este caso para
            XavaException, ObjectNotFoundException,// asociar cada pedido a la factura
            FinderException, RemoteException
    {
        super.associateEntity(clave); // Ejecuta la lógica estándar 
        Pedido pedido = (Pedido) MapFacade.findEntity("Pedido", clave); 
        pedido.copiarDetallesAFactura(); // Delega el trabajo principal en la entidad 
    }
}