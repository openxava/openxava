package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

@Entity @Getter @Setter
public class OrderDetailNoId extends Identifiable {

	@ManyToOne(fetch=FetchType.LAZY, optional=true)
	@ReferenceView("Simplest")
	private Customer customer;
	
	@ElementCollection
	@ListProperties("product.code, product.description, product.unitPrice")
	Collection<Detail> detail;
	
	@ElementCollection
	@ListProperties("product.code, product.description, product.unitPrice")
	Collection<Detail2> detail2;
	
}
