package org.openxava.test.dashboards;

import java.math.*;
import java.util.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.test.model.*;

/**
 * In dashboards package to test transient classes not in model package.
 *  
 * @since 7.4
 * @author Javier Paniza
 */
@View(members=
	"staffCount, averageAge, menPercentage, womanPercentage; " + 
	"turnoverEvolution;" +
	"turnover, illnessRate, accidentRate, maternityRate;" + 
	"turnoverByYear, moreSeniorWorkers"
)

public class StaffDashboard {
	
	@LargeDisplay(icon="account-group")
	public int getStaffCount() {
		// Instead of return an ad hoc number write your own logic
		// with JPA queries and Java to obtain the returned value
		return 223; 
	}
	
	@LargeDisplay(icon="cake-variant")
	public int getAverageAge() { 
		// Instead of return an ad hoc number write your own logic
		// with JPA queries and Java to obtain the returned value		
		return 37; 
	}
		
	@LargeDisplay(suffix="%", icon="face") // Always "face" icon to test a case with icon mapping
	public BigDecimal getMenPercentage() {
		// Instead of return an ad hoc number write your own logic
		// with JPA queries and Java to obtain the returned value
		return new BigDecimal("34.27"); 
	}
	
	@LargeDisplay(suffix="%", icon="face-woman")
	public BigDecimal getWomanPercentage() { 
		// Instead of return an ad hoc number write your own logic
		// with JPA queries and Java to obtain the returned value		
		return new BigDecimal("65.73"); 
	}
	
	@LargeDisplay(suffix="%", icon="account-convert")
	public int getTurnover() {
		// Instead of return an ad hoc number write your own logic
		// with JPA queries and Java to obtain the returned value
		return 4; 
	}	
	
	@LargeDisplay(suffix="%", icon="hotel")
	public BigDecimal getIllnessRate() {
		// Instead of return an ad hoc number write your own logic
		// with JPA queries and Java to obtain the returned value
		return new BigDecimal("2.13"); 
	}
	
	@LargeDisplay(suffix="%", icon="bandage")
	public BigDecimal getAccidentRate() {
		// Instead of return an ad hoc number write your own logic
		// with JPA queries and Java to obtain the returned value		
		return new BigDecimal("0.03"); 
	}		
	
	@LargeDisplay(suffix="%", icon="baby-carriage")
	public BigDecimal getMaternityRate() {
		// Instead of return an ad hoc number write your own logic
		// with JPA queries and Java to obtain the returned value		
		return new BigDecimal("0.91"); 
	}		
		
	@Chart
	public Collection<StaffTurnover> getTurnoverEvolution() {
		// Here we create some ad hoc elements, but you shoud
		// write your own logic with JPA queries, Java or both
		// to get the list
		Collection<StaffTurnover> result = new ArrayList<>();
		result.add(new StaffTurnover(2020, 9, 5));
		result.add(new StaffTurnover(2021, 12, 3));
		result.add(new StaffTurnover(2022, 14, 1));
		result.add(new StaffTurnover(2023, 4, 16));
		result.add(new StaffTurnover(2024, 3, 21));
		return result;
	}
	
	@SimpleList 
	public Collection<StaffTurnover> getTurnoverByYear() {
		// An example that we can see the same data with different format
		return getTurnoverEvolution();
	}	
	
	
	@ReadOnly // Not needed @SimpleList are read only by default
	@ViewAction("") // Not needed @SimpleList has not actions by default
	@SimpleList 
	public Collection<Worker> getMoreSeniorWorkers() {
		// An example using JPA, note the setMaxResults(5) to limit the size
		return XPersistence.getManager().createQuery("from Worker").setMaxResults(5).getResultList();
	}
	
}
