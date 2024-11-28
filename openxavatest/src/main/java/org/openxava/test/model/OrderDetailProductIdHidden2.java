package org.openxava.test.model;

import javax.persistence.*;

import lombok.*;

/**
 * 
 * @author Chungyen Tsai
 *
 */

@Embeddable @Getter @Setter
public class OrderDetailProductIdHidden2 {

	int quantity;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=true)
	ProductIdHidden2 product;
	
}
