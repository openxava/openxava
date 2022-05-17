package org.openxava.test.model;

import org.openxava.annotations.*;
import javax.persistence.*;

@Entity
@IdClass(EmployeeKey.class)
@Tab(properties="id, name, info.position, info.seniority")
public class Employee {

	@Id 
	private Integer id;
	
	@Id @Column(length=40)	
	private String name;

	@OneToOne
	@PrimaryKeyJoinColumns({
		@PrimaryKeyJoinColumn(name="ID",
			referencedColumnName="EMP_ID"),
		@PrimaryKeyJoinColumn(name="NAME",
			referencedColumnName="EMP_NAME")
	})
	private EmployeeInfo info;

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

	public EmployeeInfo getInfo() {
		return info;
	}

	public void setInfo(EmployeeInfo info) {
		this.info = info;
	}

}
