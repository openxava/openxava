package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/** 
 * For test elementCollection with visible key and not listed in listProperties
 * 
 * @author Chungyen Tsai
 */

@Entity @Getter @Setter
@Table(name="PRODUCTIDHIDDEN")
public class ProductIdVisible {
	
	@Id @Column(length=10)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer number;
	
	private int code;
	
	@Column(length=40) @Required
	private String description;
	
	private BigDecimal unitPrice;

}
