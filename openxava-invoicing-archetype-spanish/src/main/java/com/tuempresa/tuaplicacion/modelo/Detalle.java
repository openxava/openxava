package com.tuempresa.tuaplicacion.modelo;
 
import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.tuempresa.tuaplicacion.calculadores.*;

import lombok.*;
 
@Embeddable @Getter @Setter
public class Detalle {
 
    int cantidad;
 
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    Producto producto;
    
    @Money
    @Depends("precioPorUnidad, cantidad") // precioPorUnidad en vez de producto.numero
    public BigDecimal getImporte() {
        if (precioPorUnidad == null) return BigDecimal.ZERO; // precioPorUnidad en vez de producto y producto.getPrecio()
        return new BigDecimal(cantidad).multiply(precioPorUnidad); // precioPorUnidad en vez de producto.getPrecio()
    }
 
    
    @DefaultValueCalculator(
    	    value=CalculadorPrecioPorUnidad.class, // Esta clase calcula el valor inicial
    	    properties=@PropertyValue(
    	        name="numeroProducto", // La propiedad numeroProducto del calculador...
    	        from="producto.numero") // ... se llena con el valor de producto.numero de la entidad
    	)
    @Money
    BigDecimal precioPorUnidad; // Una propiedad persistente convencional
}