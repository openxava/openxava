package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/**
 * 
 * @since 7.0.5
 * @author Javier Paniza
 */

@Entity @Getter @Setter
public class AnnualContribution {
	
	@Id @Column(length=4)
	int year;
	
	@ElementCollection
	@ListProperties("description, pieces, amount, total")
	Collection<ContributionDetail> contributions;

}
