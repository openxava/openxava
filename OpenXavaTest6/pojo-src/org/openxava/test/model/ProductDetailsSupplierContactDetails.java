package org.openxava.test.model;

import javax.persistence.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity
public class ProductDetailsSupplierContactDetails extends Identifiable {
	
	@ManyToOne
	private Product5 product;
	
	@Column(length=40)
	private String suplier;

	@Column(length=100)	
	private String details;

	public Product5 getProduct() {
		return product;
	}

	public void setProduct(Product5 product) {
		this.product = product;
	}

	public String getSuplier() {
		return suplier;
	}

	public void setSuplier(String suplier) {
		this.suplier = suplier;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}
	
}
