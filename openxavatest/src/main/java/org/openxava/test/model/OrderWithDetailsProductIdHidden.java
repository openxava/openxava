package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

/**
 * 
 * @author Chungyen Tsai
 *
 */

@Entity @Getter @Setter
public class OrderWithDetailsProductIdHidden extends Identifiable {

	@ManyToOne(fetch=FetchType.LAZY, optional=true)
	@ReferenceView("Simplest")
	private Customer customer;
	
	@ElementCollection
	@ListProperties("product.code, product.description")
	Collection<OrderDetailProductIdHidden> details;
	
	@ElementCollection
	@ListProperties("product.code, product.description")
	Collection<OrderDetailProductIdHidden2> details2;
	
}
