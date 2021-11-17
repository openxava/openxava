package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.calculators.*;

/**
 * 
 * @author Javier Paniza
 */

@Embeddable
public class ProductExpense2 { 
	
	// TMR ME QUEDÉ POR AQUÍ: HACIENDO PRUEBAS:
	// TMR - ORIGINAL NO VA (AUNQUE EL TEST DA VERDE)
	// TMR - SIN @DescriptionsList VA LA PRIMERA FILA, A PARTIR DE LA SEGUNDA NO VA
	// TMR - CON UN CAMPO PLANO (CÓDIGO ACTUAL) NO VA
	/* tmr 
	@ManyToOne(fetch=FetchType.LAZY)
	// tmr @DescriptionsList // Must be @DescriptionsList to test a case 	
	private Carrier carrier;
	*/
	
	@Column(name="carrier_number")
	private int carrierNumber; // tmr


	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(descriptionProperties="year, number")
	@DefaultValueCalculator(DefaultInvoiceCalculator.class) 		
	private Invoice invoice;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	@DefaultValueCalculator( 
		value=org.openxava.calculators.IntegerCalculator.class,
		properties={ @PropertyValue(name="value", value="2") }		
	)	
	private Product product;	


	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	/* tmr
	public Carrier getCarrier() {
		return carrier;
	}

	public void setCarrier(Carrier carrier) {
		this.carrier = carrier;
	}
	*/

	public int getCarrierNumber() {
		return carrierNumber;
	}

	public void setCarrierNumber(int carrierNumber) {
		this.carrierNumber = carrierNumber;
	}
	
	
	
}
