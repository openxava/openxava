package com.tuempresa.tuaplicacion.modelo;

import java.math.*;
import java.time.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.model.*;

import lombok.*;

// RENOMBRA ESTA CLASE COMO Factura, OrdenCompra, ParteTrabajo, Albaran, Cuenta, Envio, etc.

// PUEDES RENOMBRAR LOS MIEMBROS ABAJO A TU CONVENIENCIA, 
// POR EJEMPLO Persona persona POR Cliente cliente, 
// PERO CAMBIA TODAS LAS REFERENCIAS EN EL CÓDIGO USANDO BUSCAR Y REEMPLAZAR PARA EL PROYECTO. 
// NO USES REFACTOR > RENAME PARA LOS MIEMBROS PORQUE NO CAMBIA EL CONTENIDO EN LAS ANOTACIONES.

@Entity @Getter @Setter
@View(members=
	"anyo, numero, fecha;" +
	"persona;" +
	"detalles { detalles };" +
	"observaciones { observaciones }"
)
@Tab(properties="anyo, numero, fecha, persona.nombre, observaciones")
public class Maestro extends Identifiable {
	
	@DefaultValueCalculator(CurrentYearCalculator.class)
	@Column(length=4) @Required
	int anyo;
	
	@Column(length=6) @Required
	int numero;
	
	@Required @DefaultValueCalculator(CurrentLocalDateCalculator.class) 
	LocalDate fecha;
	
	@Column(length=2) @Required
	@DefaultValueCalculator(value=IntegerCalculator.class, properties=@PropertyValue(name="value", value="21"))
	int porcentajeIVA;
	
	@ReferenceView("Simple") 
	@ManyToOne(optional=false, fetch=FetchType.LAZY)
	Persona persona;
	
	@ElementCollection @OrderColumn
	@ListProperties("item.codigo, item.descripcion, precioUnitario, cantidad, importe[maestro.suma, maestro.porcentajeIVA, maestro.iva, maestro.total]")
	List<Detalle> detalles;
	
	@HtmlText
	String observaciones;
	
	public BigDecimal getSuma() {
		BigDecimal sum = BigDecimal.ZERO;
		for (Detalle detalle: detalles) {
			sum = sum.add(detalle.getImporte());
		}
		return sum;
	}
	
	@Depends("sum, taxPercentage")
	public BigDecimal getIva() {
		return getSuma().multiply(new BigDecimal(getPorcentajeIVA()).divide(new BigDecimal(100))).setScale(2, RoundingMode.UP);
	}
	
	@Depends("sum, tax")
	public BigDecimal getTotal() {
		return getSuma().add(getIva()).setScale(2, RoundingMode.UP);
	}
	
}
