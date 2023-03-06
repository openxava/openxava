package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.annotations.File;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */
@Embeddable @Getter @Setter
public class TravelExpense {

	@Column(length = 50)
	String description;
	
	@Money
	@Column(precision = 15, scale = 2)
	BigDecimal amount;
	
	@File
	@Column(length = 32)
	String file;

}
