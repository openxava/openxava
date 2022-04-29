package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class Worker {

	@Id @GeneratedValue @Hidden
	private Integer id;
	
	@Column(length=10) @Required
	private String nickName;
	
	@Column(length=40) @Required
	private String fullName;
	
	private BigDecimal hourPrice; 

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public BigDecimal getHourPrice() {
		return hourPrice;
	}

	public void setHourPrice(BigDecimal hourPrice) {
		this.hourPrice = hourPrice;
	}
	
	
}
