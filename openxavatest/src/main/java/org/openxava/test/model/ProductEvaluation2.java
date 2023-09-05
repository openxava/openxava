package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */

@Embeddable @Getter @Setter
public class ProductEvaluation2 {

		
	@ManyToOne(fetch=FetchType.LAZY)
	// TMR @DescriptionsList // ESTO ESTABA PERO ES REBUZNANTE
	@DescriptionsList(
		 depends="family", 
		 condition="${family.number} = ?" 
	)
	Product2 product;

	@Column(length=40) 
	String evaluation;
		
}
