package org.openxava.test.model;

import java.util.*;
import javax.persistence.*;
import org.openxava.jpa.*;
import org.openxava.model.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */
@Entity
public class WeeklyActivity extends Identifiable {

	@ManyToOne
	private MonthlyActivity monthlyActivity;
	
	@OneToMany(mappedBy="weeklyActivity", cascade=CascadeType.ALL)
	private Collection<DailyActivity> dailyActivities;
	
	public void createActivities() {   
		/*
		System.out.println("[Clase2.generaClasesUno] "); // tmp
		System.out.println("[Clase2.generaClasesUno] getClases()=" + getClases()); // tmp
	    if(getClases() != null) {           
	        Clase1 cc1 = new Clase1();
	        cc1.setNombre("xx");
	        XPersistence.getManager().persist(cc1);
	        clases.add(cc1);            
	        System.out.println("[Clase2.generaClasesUno] ADDED"); // tmp
	    } 
	    */      
	}

	public MonthlyActivity getMonthlyActivity() {
		return monthlyActivity;
	}

	public void setMonthlyActivity(MonthlyActivity monthlyActivity) {
		this.monthlyActivity = monthlyActivity;
	}

}
