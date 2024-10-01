package com.tuempresa.tuaplicacion.calculadores; // En el paquete calculadores
 
//Para usar getManager()
import static org.openxava.jpa.XPersistence.getManager;

import org.openxava.calculators.*;

import com.tuempresa.tuaplicacion.modelo.*;

import lombok.*;
 
public class CalculadorPrecioPorUnidad implements ICalculator {
 
    @Getter @Setter
    int numeroProducto;
 
    @Override
    public Object calculate() throws Exception {
        Producto producto = getManager() // getManager() de XPersistence
            .find(Producto.class, numeroProducto); // Busca el producto
        return producto.getPrecio();    // Retorna su precio
    }
 
}