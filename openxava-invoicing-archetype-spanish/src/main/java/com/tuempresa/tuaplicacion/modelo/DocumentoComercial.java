package com.tuempresa.tuaplicacion.modelo;

import java.math.*;
import java.time.*;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.jpa.*;

import com.tuempresa.tuaplicacion.calculadores.*;

import lombok.*;

@Entity @Getter @Setter
@View(members=
"anyo, numero, fecha," + // Los miembros para la cabecera en una línea
"datos {" + // Una pestaña 'datos' para los datos principales del documento
    "cliente;" +
    "detalles;" +
    "observaciones" +
"}")
abstract public class DocumentoComercial extends Eliminable{

	@Column(length=4)
    @DefaultValueCalculator(CurrentYearCalculator.class) // Año actual
	@SearchKey
    int anyo;
 
	@Column(length = 6)
	@ReadOnly // El usuario no puede modificar el valor
	@SearchKey
	int numero;
 
    @Required
    @DefaultValueCalculator(CurrentLocalDateCalculator.class) // Fecha actual
    LocalDate fecha;
 
    @ManyToOne(fetch=FetchType.LAZY, optional=false) // El cliente es obligatorio
    @ReferenceView("Simple") // La vista llamada 'Simple' se usará para visualizar esta referencia
    Cliente cliente;
    
    @ElementCollection
    @ListProperties(
            "producto.numero, producto.descripcion, cantidad, precioPorUnidad, " +
            "importe+[" +
            	"documentoComercial.porcentajeIVA," +
            	"documentoComercial.iva," +
            	"documentoComercial.importeTotal" +
            "]" 
        )	
    Collection<Detalle> detalles;
    
    @TextArea
    String observaciones;
    
    @Digits(integer=2, fraction=0) // Para indicar su tamaño
    @DefaultValueCalculator(CalculadorPorcentajeIVA.class)
    BigDecimal porcentajeIVA;
    
    @ReadOnly
    @Money
    @Calculation("sum(detalles.importe) * porcentajeIVA / 100")
    BigDecimal iva;
  
    @org.hibernate.annotations.Formula("IMPORTETOTAL * 0.10") // El cálculo usando SQL
    @Setter(AccessLevel.NONE) // El setter no se genera, sólo necesitamos el getter
    @Money
    BigDecimal beneficioEstimado; // Un campo, como con una propiedad persistente
    
    @ReadOnly
    @Money
    @Calculation("sum(detalles.importe) + iva")    
    BigDecimal importeTotal;    

    @PrePersist // Ejecutado justo antes de grabar el objeto por primera vez
    private void calcularNumero() {
        Query query = XPersistence.getManager().createQuery(
            "select max(f.numero) from " +
            getClass().getSimpleName() + // De esta forma es válido para Factura y Pedido
            " f where f.anyo = :anyo");
        query.setParameter("anyo", anyo);
        Integer ultimoNumero = (Integer) query.getSingleResult();
        this.numero = ultimoNumero == null ? 1 : ultimoNumero + 1;
    }
    
    public String toString() {
        return anyo + "/" + numero;
    }
    
    
}
