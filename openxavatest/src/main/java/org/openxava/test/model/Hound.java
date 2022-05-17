package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.DefaultValueCalculator;
import org.openxava.annotations.ReferenceView;
import org.openxava.annotations.View;
import org.openxava.annotations.Views;
import org.openxava.calculators.CurrentDateCalculator;
import org.openxava.util.Dates;
import org.openxava.util.XavaResources;

/**
 * To test @javax.validation.constraints.Size with min and max elements on a 
 * Collection of entities not embeddable.
 * 	
 * @author Jeromy Altuna
 */
@Entity
@Views({
	@View(extendsView="super.DEFAULT", 
		members="birth; hunter { hunter }"
	)
})
public class Hound extends HunterAndHound {
	
	@DefaultValueCalculator(CurrentDateCalculator.class)
	private Date birth;
	
	@ManyToOne
	@ReferenceView("NoHounds")
	private Hunter hunter;
	
	@PreUpdate
	private void validate() throws Exception {
		if (hunter != null && !hasTraining()) {
			throw new javax.validation.ValidationException(
				XavaResources.getString("untrained_hound", getName())
			);
		}
	}
	
	public boolean hasTraining() {
		if (birth == null) return true;
		return Dates.dateDistance(new Date(), birth).years >= 2;
	}

	public Hunter getHunter() {
		return hunter;
	}
	public void setHunter(Hunter hunter) {
		this.hunter = hunter;
	}

	public Date getBirth() {
		return birth;
	}
	public void setBirth(Date birth) {
		this.birth = birth;
	}	
}
