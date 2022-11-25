package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * tmr
 * @author Javier Paniza
 */

@Embeddable @Getter @Setter
public class ProductEvaluation2 {

		
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList
	Product2 product;

	@Column(length=40) 
	String evaluation;
		
}
