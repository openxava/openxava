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
	// tmr ini
	@View(name="EmployeesChart", members="name; employees")
	// tmr fin
})
public class Corporation extends Identifiable {

	@Required
	private String name;
	
	@Icon
	@Column(length=40)
	private String icon; 
	
	@OneToMany(mappedBy="corporation", cascade=CascadeType.ALL)
	// tmr @Editor(forViews="EmployeesChart", value="EmployeesChart") // tmr
	// tmr @Chart(labelProperties = "firstName, lastName", dataProperties = "salary, bonus") // tmr
	// tmr @Chart
	@Chart(forViews="EmployeesChart") // TMR ME QUEDÉ PARA EMPEZAR CON ESTO, EL SOPORTE DE forViews 
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
