package org.openxava.test.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.*;

@Entity
public class ConversionFactor implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	@Column(length = 30)
	private String fromUnit;
	
	@Column(length = 30)
	private String toUnit;
	
	@Column(scale = 6)
	private BigDecimal factor;
	
	@Digits(integer=10, fraction=6) 
	private BigDecimal reverseFactor;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFromUnit() {
		return fromUnit;
	}

	public void setFromUnit(String fromUnit) {
		this.fromUnit = fromUnit;
	}

	public String getToUnit() {
		return toUnit;
	}

	public void setToUnit(String toUnit) {
		this.toUnit = toUnit;
	}

	public BigDecimal getFactor() {
		return factor;
	}

	public void setFactor(BigDecimal factor) {
		this.factor = factor;
	}

	public BigDecimal getReverseFactor() {
		return reverseFactor;
	}

	public void setReverseFactor(BigDecimal reverseFactor) {
		this.reverseFactor = reverseFactor;
	}

}
