package com.tuempresa.tuaplicacion.modelo;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import com.tuempresa.tuaplicacion.calculadores.*;

import lombok.*;

// RENOMBRA ESTA CLASE COMO DetalleFactura, DetalleCompra, DetalleTrabajo, LineaAlbaran, MovimientoCuenta, DetalleEnvio, etc.

// PUEDES RENOMBRAR LOS MIEMBROS ABAJO A TU CONVENIENCIA, 
// POR EJEMPLO precioUnitario POR precioHora, 
// PERO CAMBIA TODAS LAS REFERENCIAS EN EL CÓDIGO USANDO BUSCAR Y REEMPLAZAR PARA EL PROYECTO. 
// NO USES REFACTOR > RENAME PARA LOS MIEMBROS PORQUE NO CAMBIA EL CONTENIDO EN LAS ANOTACIONES.

@Embeddable @Getter @Setter
public class Detalle {
		
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	Item item;

	@Required 
	@DefaultValueCalculator(  
		value=CalculadorPrecioUnitario.class,
		properties=@PropertyValue(
			name="codigo",
			from="item.codigo")
	)
	BigDecimal precioUnitario;
		
	@Required
	int cantidad;	

	@Depends("precioUnitario, cantidad") 
	public BigDecimal getImporte() {
		return new BigDecimal(getCantidad()).multiply(getPrecioUnitario()); 
	}

	public BigDecimal getPrecioUnitario() {
		return precioUnitario == null?new BigDecimal("0.00"):precioUnitario;
	}
	
}
