package org.openxava.test.model;

import jakarta.persistence.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 * @since 7.7
 */

@Embeddable @Getter @Setter
public class ProcessedWeighingDetail {
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private Weighing weighing;
}
