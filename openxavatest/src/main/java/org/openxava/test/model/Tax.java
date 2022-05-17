package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;
import javax.persistence.Entity;

import org.hibernate.annotations.Formula;

/**
 * Create on 22/11/2013 (13:31:08)
 * @author Ana Andres
 */

@Entity
public class Tax {
	
	@Id
	private int code;
	
	private String name;
	
	private String situation;
	
	private BigDecimal firstAmount;
	
	private BigDecimal secondAmount;
	
	private BigDecimal thirdAmount;
	
	private BigDecimal fourthAmount;
	
	private BigDecimal fifthAmount;

	@Formula("firstAmount + secondAmount + thirdAmount + fourthAmount + fifthAmount")
	private BigDecimal total;
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSituation() {
		return situation;
	}

	public void setSituation(String situation) {
		this.situation = situation;
	}

	public BigDecimal getFirstAmount() {
		return firstAmount;
	}

	public void setFirstAmount(BigDecimal firstAmount) {
		this.firstAmount = firstAmount;
	}

	public BigDecimal getSecondAmount() {
		return secondAmount;
	}

	public void setSecondAmount(BigDecimal secondAmount) {
		this.secondAmount = secondAmount;
	}

	public BigDecimal getThirdAmount() {
		return thirdAmount;
	}

	public void setThirdAmount(BigDecimal thirdAmount) {
		this.thirdAmount = thirdAmount;
	}

	public BigDecimal getFourthAmount() {
		return fourthAmount;
	}

	public void setFourthAmount(BigDecimal fourthAmount) {
		this.fourthAmount = fourthAmount;
	}

	public BigDecimal getFifthAmount() {
		return fifthAmount;
	}

	public void setFifthAmount(BigDecimal fifthAmount) {
		this.fifthAmount = fifthAmount;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
}
