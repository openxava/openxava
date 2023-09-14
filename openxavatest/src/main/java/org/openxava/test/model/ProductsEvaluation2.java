package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

import lombok.*;

/**
 * 						
 * @author Javier Paniza 
 */

@Entity @Getter @Setter
@View(name="ExtendedDetails") 
public class ProductsEvaluation2 extends Identifiable {
	
	@Column(length=40) @Required
	String description;
	
	@DescriptionsList
	@ManyToOne(fetch = FetchType.LAZY)
	Family2 family;
	
	@ElementCollection
	@ListProperties("product, evaluation")
	@ListProperties(forViews="ExtendedDetails", value="product, evaluation, product.color.name")  	
	Collection<ProductEvaluation2> evaluations;

}
