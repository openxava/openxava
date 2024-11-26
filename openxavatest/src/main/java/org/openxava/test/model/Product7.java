package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/** 
 * For test elementCollection with hidden key and not listed in listProperties
 * 
 * @author Chungyen Tsai
 */

@Entity @Getter @Setter
@Table(name="PRODUCT")
public class Product7 {
	
	@Id @Column(length=10) @Hidden
	private long number;
	
	private long code;
	
	@Column(length=40) @Required
	private String description;
	
	private BigDecimal unitPrice;

}
