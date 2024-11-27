package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

import lombok.*;

/** 
 * For test elementCollection with wrapper hidden key and not listed in listProperties
 * 
 * @author Chungyen Tsai
 */

@Entity @Getter @Setter
public class ProductIdHidden {
	
	@Id @Column(length=10) @Hidden
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer number;
	
	private int code;
	
	@Column(length=40) @Required
	private String description;

}
