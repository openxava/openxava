package org.openxava.test.model;

import jakarta.persistence.*;

import lombok.*;

/**
 * 
 * @author Chungyen Tsai
 * 
 */

@Embeddable @Getter @Setter
public class OrderDetailProductIdHidden {

	int quantity;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	ProductIdHidden product;
}
