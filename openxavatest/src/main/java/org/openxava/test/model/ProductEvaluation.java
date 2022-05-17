package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Embeddable
public class ProductEvaluation {

		
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	@ReadOnly
	private Product product;

	@Column(length=40) 
	private String evaluation;


	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(String evaluation) {
		this.evaluation = evaluation;
	}
		
}
