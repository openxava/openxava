package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Laurent Wibaux 
 */

@Entity
@Views({
	@View(members="name, icon; employees {employees}"), 
	@View(name="Simple", members="name"),
	@View(name="EmployeesChart", members="name; employees"),
	@View(name="EmployeesRefinedChart", members="name; employees")
})
public class Corporation extends Identifiable {

	@Required
	private String name;
	
	@Icon
	@Column(length=40)
	@DisplaySize(1) // tmr
	private String icon; 
	
	@OneToMany(mappedBy="corporation", cascade=CascadeType.ALL)
	@Chart(forViews="EmployeesChart")
	@Chart(forViews="EmployeesRefinedChart", labelProperties = "firstName, lastName", dataProperties = "salary")
	private Collection<CorporationEmployee> employees;
	
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setEmployees(Collection<CorporationEmployee> employees) {
		this.employees = employees;
	}

	public Collection<CorporationEmployee> getEmployees() {
		return employees;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
}
