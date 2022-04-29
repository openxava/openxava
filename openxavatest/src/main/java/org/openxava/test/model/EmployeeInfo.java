package org.openxava.test.model;

import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@IdClass(EmployeeInfoKey.class)
public class EmployeeInfo {
	
	@Id @Column(name="EMP_ID") 
	private Integer id;
	
	@Id @Column(name="EMP_NAME", length=40)
	private String name;

	@Column(name="EMP_POSITION", length=40)
	private String position;
	
	@Column(name="EMP_SENIORITY", length=20)
	private String seniority;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getSeniority() {
		return seniority;
	}

	public void setSeniority(String seniority) {
		this.seniority = seniority;
	}

}
