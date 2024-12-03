package org.openxava.test.model;

import javax.persistence.*;

import lombok.*;

/**
 * With product id hidden and optional true
 * 
 * @author Chungyen Tsai
 *
 */

@Embeddable @Getter @Setter
public class OrderDetailProductIdHidden2 {

	int quantity;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=true) // keep optional = true
	ProductIdHidden2 product;
	
}
