package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import lombok.*;

/**
 * tmr
 * 
 * @since 7.0.5
 * @author Javier Paniza
 */

@Entity @Getter @Setter
public class AnnualContribution {
	
	@Id @Column(length=4)
	int year;
	
	@ElementCollection
	Collection<ContributionDetail> contributions;

}
