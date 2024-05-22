package com.tuempresa.tuaplicacion.modelo;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

// RENAME THIS CLASS AS InvoiceDetail, PurchaseOrderDetail, WorkOrderDetail, DeliveryDetail, AcountTransaction, ShipmentDetail, etc.

// YOU CAN RENAME THE MEMBERS BELOW AT YOUR CONVENIENCE, 
// FOR EXAMPLE unitPrice BY hourPrice,
// BUT CHANGE ALL REFERENCES IN ALL CODE USING SEARCH AND REPLACE FOR THE PROJECT. 
// DON'T USE REFACTOR > RENAME FOR MEMBERS BECAUSE IT DOESN'T CHANGE THE ANNOTATIONS CONTENT.

@Embeddable @Getter @Setter
public class Detalle {
		
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	Item item;

	// TMR ME QUEDÉ POR AQUÍ: ACABÉ DE TRADUCIR LA CLSAE, FALTA AÑADIR EL CALCULADOR
	@Required 
	/* tmr FALTA ESTO
	@DefaultValueCalculator(  
		value=UnitPriceCalculator.class,
		properties=@PropertyValue(
			name="codigo",
			from="item.codigo")
	)
	*/
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
