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
	"staffCount, averageAge, tenureYears, turnover, menPercentage; " +
	"turnoverEvolution;" +
	"womanPercentage, illnessRate, accidentRate, maternityRate, paternityRate;" +
	"turnoverByYear, moreSeniorWorkers"
)

public class StaffDashboard {
	
	// TMR ME QUEDÉ POR AQUÍ. FALTARÍA REORDENAR NÚMEROS GRANDES Y PONERLES ICONOS
	
	@LargeDisplay
	public int getStaffCount() { // tmr i18n 
		return 683; // tmr Poner alguna lógica
	}
	
	@LargeDisplay
	public int getAverageAge() { // tmr i18n
		return 41; // tmr Poner alguna lógica
	}
	
	@LargeDisplay
	public int getTenureYears() { // tmr i18n
		return 5; // tmr Poner alguna lógica
	}
	
	@LargeDisplay(suffix="%")
	public int getTurnover() { // tmr i18n
		return 5; // tmr Poner alguna lógica
	}
	
	@LargeDisplay(suffix="%")
	public BigDecimal getMenPercentage() { // tmr i18n
		return new BigDecimal("31.17"); // tmr Poner alguna lógica
	}
	
	@LargeDisplay(suffix="%")
	public BigDecimal getWomanPercentage() { // tmr i18n
		return new BigDecimal("67.12"); // tmr Poner alguna lógica
	}
	
	@LargeDisplay(suffix="%")
	public BigDecimal getIllnessRate() { // tmr i18n
		return new BigDecimal("1.22"); // tmr Poner alguna lógica
	}
	
	@LargeDisplay(suffix="%")
	public BigDecimal getAccidentRate() { // tmr i18n
		return new BigDecimal("0.08"); // tmr Poner alguna lógica
	}		
	
	@LargeDisplay(suffix="%")
	public BigDecimal getMaternityRate() { // tmr i18n
		return new BigDecimal("0.62"); // tmr Poner alguna lógica
	}		
	
	@LargeDisplay(suffix="%")
	public BigDecimal getPaternityRate() { // tmr i18n
		return new BigDecimal("0.04"); // tmr Poner alguna lógica
	}
	
	// tmr Sale añor repetido 2021, 2021
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
	
	
	@ReadOnly @ViewAction("") // To change by @SimpleList when available
	public Collection<StaffTurnover> getTurnoverByYear() {
		return getTurnoverEvolution();
	}	
	
	
	@ReadOnly @ViewAction("") // To change by @SimpleList when available
	public Collection<Worker> getMoreSeniorWorkers() {
		return XPersistence.getManager().createQuery("from Worker").setMaxResults(5).getResultList();
	}
	
	
	
	
}
