package org.openxava.test.model;

import java.math.*;

import org.openxava.annotations.*;

/**
 * tmr
 * 
 * @since 7.4
 * @author Javier Paniza
 */
@View(members=
	"staffCount, averageAge, tenureYears, turnover, menPercentage; " +
	"womanPercentage, illnessRate, accidentRate, maternityRate, paternityRate"
)

public class StaffDashboard {
	
	// TMR ME QUEDÉ POR AQUÍ. PRIMERAS PRUEBAS VISUALES, CON ALGÚN AJUSTE EN EL CSS
	
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
	

}
