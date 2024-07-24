package org.openxava.test.model;

import java.math.*;
import java.util.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

/**
 * tmr
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
	
	// TMR ME QUEDÉ POR: YA TENGO UN DASHBOARD DECENTE. DECIDIR SI DEBERÍA IMPLEMENTAR LO DE ABAJO. CAMBIAR NÚMERO SI NO. REVISAR TODO EL CÓDIGO
	
	@LargeDisplay(icon="account-group")
	public int getStaffCount() {  
		return 683; // tmr Poner alguna lógica
	}
	
	@LargeDisplay(icon="cake-variant")
	public int getAverageAge() { 
		return 41; // tmr Poner alguna lógica
	}
		
	@LargeDisplay(suffix="%", icon="face")
	public BigDecimal getMenPercentage() { 
		return new BigDecimal("31.17"); // tmr Poner alguna lógica
	}
	
	@LargeDisplay(suffix="%", icon="face-woman")
	public BigDecimal getWomanPercentage() { 
		return new BigDecimal("67.12"); // tmr Poner alguna lógica
	}
	
	@LargeDisplay(suffix="%", icon="account-convert")
	public int getTurnover() { 
		return 5; // tmr Poner alguna lógica
	}	
	
	@LargeDisplay(suffix="%", icon="hotel")
	public BigDecimal getIllnessRate() { 
		return new BigDecimal("1.22"); // tmr Poner alguna lógica
	}
	
	@LargeDisplay(suffix="%", icon="bandage")
	public BigDecimal getAccidentRate() { 
		return new BigDecimal("0.08"); // tmr Poner alguna lógica
	}		
	
	@LargeDisplay(suffix="%", icon="baby-carriage")
	public BigDecimal getMaternityRate() { 
		return new BigDecimal("0.62"); // tmr Poner alguna lógica
	}		
		
	// tmr Salían añor repetido 2021, 2021, arreglado
	@Chart
	public Collection<StaffTurnover> getTurnoverEvolution() {
		Collection<StaffTurnover> result = new ArrayList<>();
		result.add(new StaffTurnover(2020, 9, 5));
		result.add(new StaffTurnover(2021, 12, 3));
		result.add(new StaffTurnover(2022, 14, 1));
		result.add(new StaffTurnover(2024, 4, 16));
		result.add(new StaffTurnover(2024, 3, 21));
		return result;
	}
	
	
	@ReadOnly @ViewAction("") // To be changed by @SimpleList when available
	public Collection<StaffTurnover> getTurnoverByYear() {
		return getTurnoverEvolution();
	}	
	
	
	@ReadOnly @ViewAction("") // To be changed by @SimpleList when available
	public Collection<Worker> getMoreSeniorWorkers() {
		return XPersistence.getManager().createQuery("from Worker").setMaxResults(5).getResultList();
	}
	
}
