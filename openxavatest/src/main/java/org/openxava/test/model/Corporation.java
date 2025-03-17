package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;
import org.openxava.jpa.XPersistence;

/**
 * 
 * @author Laurent Wibaux 
 */

@Entity
@Views({
	@View(members="name, icon; employees {employees}"), 
	@View(name="Simple", members="name"),
	@View(name="EmployeesChart", members="name; employees"),
	@View(name="EmployeesRefinedChart", members="name; employees"),
	@View(name="EmployeesLinePieCharts", members="name; employees; externalEmployeesRatio")
})
public class Corporation extends Identifiable {

	@Required
	private String name;
	
	@Icon
	@Column(length=40)
	private String icon; 
	
	@OneToMany(mappedBy="corporation", cascade=CascadeType.ALL)
	@Chart(forViews="EmployeesChart")
	@Chart(forViews="EmployeesRefinedChart", labelProperties = "firstName, lastName", dataProperties = "salary")
	@Chart(forViews="EmployeesLinePieCharts", type=ChartType.LINE) 
	private Collection<CorporationEmployee> employees;

	@Chart(type = ChartType.PIE)
	public Collection<Ratio> getExternalEmployeesRatio() { 
		EntityManager em = XPersistence.getManager();
		
		// Query to count internal employees (email contains corporation name)
		Query internalQuery = em.createQuery(
			"SELECT COUNT(e) FROM CorporationEmployee e " +
			"WHERE e.corporation.id = :corporationId AND LOWER(e.email) LIKE :pattern");
		internalQuery.setParameter("corporationId", getId());
		internalQuery.setParameter("pattern", "%" + name.toLowerCase() + "%");
		Long internalCount = (Long) internalQuery.getSingleResult();
		
		// Query to count external employees (email does not contain corporation name)
		Query externalQuery = em.createQuery(
			"SELECT COUNT(e) FROM CorporationEmployee e " +
			"WHERE e.corporation.id = :corporationId AND LOWER(e.email) NOT LIKE :pattern");
		externalQuery.setParameter("corporationId", getId());
		externalQuery.setParameter("pattern", "%" + name.toLowerCase() + "%");
		Long externalCount = (Long) externalQuery.getSingleResult();
		
		// Create and return the collection of ratios
		Collection<Ratio> ratios = new ArrayList<>();
		ratios.add(new Ratio("Internos", internalCount.intValue()));
		ratios.add(new Ratio("Externos", externalCount.intValue()));

		System.out.println("Internos: " + internalCount + ", Externos: " + externalCount);
		
		return ratios;
	}
	
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
