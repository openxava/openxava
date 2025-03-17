package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;
import org.openxava.jpa.XPersistence;
import org.apache.commons.lang3.tuple.Pair;

/**
 * 
 * @author Laurent Wibaux 
 */

@Entity
@Views({
	@View(members="name, icon; employees {employees}"), 
	@View(name="Simple", members="name"),
	// tmr @View(name="EmployeesChart", members="name; employees"),
	@View(name="EmployeesChart", members="name; employees; extenalEmployeesRatio"), // tmr
	@View(name="EmployeesRefinedChart", members="name; employees")
})
public class Corporation extends Identifiable {

	@Required
	private String name;
	
	@Icon
	@Column(length=40)
	private String icon; 
	
	@OneToMany(mappedBy="corporation", cascade=CascadeType.ALL)
	// tmr @Chart(forViews="EmployeesChart")
	//@Chart(forViews="EmployeesChart", type=ChartType.PIE) // tmr
	@Chart(forViews="EmployeesRefinedChart", labelProperties = "firstName, lastName", dataProperties = "salary")
	private Collection<CorporationEmployee> employees;

	@Chart(type = ChartType.PIE)
	public Collection<Pair<String, Integer>> getExtenalEmployeesRatio() { // tmr
		EntityManager em = XPersistence.getManager();
		
		// Consulta para contar empleados internos (email contiene el nombre de la corporación)
		Query internalQuery = em.createQuery(
			"SELECT COUNT(e) FROM CorporationEmployee e WHERE e.corporation.id = :corporationId AND LOWER(e.email) LIKE :pattern");
		internalQuery.setParameter("corporationId", getId());
		internalQuery.setParameter("pattern", "%" + name.toLowerCase() + "%");
		Long internalCount = (Long) internalQuery.getSingleResult();
		
		// Consulta para contar empleados externos (email no contiene el nombre de la corporación)
		Query externalQuery = em.createQuery(
			"SELECT COUNT(e) FROM CorporationEmployee e WHERE e.corporation.id = :corporationId AND LOWER(e.email) NOT LIKE :pattern");
		externalQuery.setParameter("corporationId", getId());
		externalQuery.setParameter("pattern", "%" + name.toLowerCase() + "%");
		Long externalCount = (Long) externalQuery.getSingleResult();
		
		// Crear y devolver la colección de pares
		Collection<Pair<String, Integer>> ratios = new ArrayList<>();
		ratios.add(Pair.of("Internal", internalCount.intValue()));
		ratios.add(Pair.of("External", externalCount.intValue()));

		System.out.println("Internal: " + internalCount + ", External: " + externalCount);
		
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
