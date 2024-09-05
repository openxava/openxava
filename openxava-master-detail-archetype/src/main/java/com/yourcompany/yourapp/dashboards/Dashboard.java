package com.yourcompany.yourapp.dashboards;

import java.math.*;
import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

@View(members=
	"numberOfMasters, numberOfPeople, totalSum; " +
	"evolution;" +
	"topPeople, topYears"
)
public class Dashboard {
	
	@Money
	@LargeDisplay(icon="cash")
	public BigDecimal getTotalSum() { 
		return (BigDecimal) XPersistence.getManager().createQuery("select sum(m.total) from Master m").getSingleResult();
	}
	
	@LargeDisplay(icon="animation")
	public long getNumberOfMasters() { 		
		return count("Master"); 
	}
		
	@LargeDisplay(icon="account-group")
	public long getNumberOfPeople() { 
		return count("Person");
	}
	
	@Chart
	public Collection<PerYear> getEvolution() { 
		String jpql = "select new com.yourcompany.yourapp.dashboards.PerYear(m.year, sum(m.total), sum(m.tax)) " +
			"from Master m " +
			"group by m.year " +
			"order by m.year asc";
		TypedQuery<PerYear> query = XPersistence.getManager().createQuery(jpql, PerYear.class);
		return query.getResultList();
	}
	
	@SimpleList 
	public Collection<PerPerson> getTopPeople() { 
		String jpql = "select new com.yourcompany.yourapp.dashboards.PerPerson(m.person.name, sum(m.total) as amount) " +
			"from Master m " +
			"group by m.person.number, amount " +
			"order by amount desc";
		TypedQuery<PerPerson> query = XPersistence.getManager().createQuery(jpql, PerPerson.class).setMaxResults(5);
		return query.getResultList();
	}
	
	@SimpleList @ListProperties("year, total")
	public Collection<PerYear> getTopYears() { 
		String jpql = "select new com.yourcompany.yourapp.dashboards.PerYear(m.year, sum(m.total) as amount, sum(m.tax)) " +
			"from Master m " +
			"group by m.year " +
			"order by amount desc";
		TypedQuery<PerYear> query = XPersistence.getManager().createQuery(jpql, PerYear.class).setMaxResults(5);
		return query.getResultList();
	}	
	
	private Long count(String entity) {
		return (Long) XPersistence.getManager().createQuery("select count(*) from " + entity).getSingleResult();
	}
	
}
