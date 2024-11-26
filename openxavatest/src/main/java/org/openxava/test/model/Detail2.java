package org.openxava.test.model;

import javax.persistence.*;

import lombok.*;

@Embeddable @Getter @Setter
public class Detail2 {

	int quantity;
	
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	ProductIdVisible product;
	
}
