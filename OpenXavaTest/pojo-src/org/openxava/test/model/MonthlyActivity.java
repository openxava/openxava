package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;
import org.openxava.model.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */

@Entity
public class MonthlyActivity extends Identifiable {

	@OneToMany(mappedBy="mothlyActivity", cascade=CascadeType.ALL)
	private Collection<WeeklyActivity> weeklyActivities;
	
	public void createActivities() {
		/* tmp
		System.out.println("[Clase3.saltaExceptionValidador1] "); // tmp
	    for(Clase2 cc2 : getClases()) {
	        cc2.generaClasesUno();
	    } 
	    */      
	}

	public Collection<WeeklyActivity> getWeeklyActivities() {
		return weeklyActivities;
	}

	public void setWeeklyActivities(Collection<WeeklyActivity> weeklyActivities) {
		this.weeklyActivities = weeklyActivities;
	}

}
