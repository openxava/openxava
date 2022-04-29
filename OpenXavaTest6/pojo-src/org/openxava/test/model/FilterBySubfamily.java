package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.actions.*;

/**
 * Example of an transient OpenXava model class (not persistent). <p>
 *
 * This can be used, for example, to display a dialog,
 * or any other graphical interface.<p>
 * 
 * Note that is not marked as @Entity <br>
 *
 * @author Javier Paniza
 */

@Views({
	@View(name="Family1", members="subfamily"),
	@View(name="Family2", members="subfamily"),
	@View(name="WithSubfamilyForm", members="subfamily"),
	@View(name="Range", members="subfamily; subfamilyTo; rangeDescription") 
})

public class FilterBySubfamily {

	@ManyToOne(fetch=FetchType.LAZY) @Required 
	@NoCreate(forViews="Family1, Family2") 
	@NoModify(forViews="Family2, WithSubfamilyForm")
	@NoSearch(forViews="WithSubfamilyForm")
	@DescriptionsLists({
		@DescriptionsList(forViews="Family1", 
			condition="${family.number} = 1", order="${number} desc"
		),
		@DescriptionsList(forViews="Family2", 
			condition="${family.number} = 2"
		)
	})	
	@OnChangeSearch(OnChangeSubfamilySearchAction.class) 	
	private Subfamily2 subfamily;
	
	@ManyToOne(fetch=FetchType.LAZY) 
	private Subfamily2 subfamilyTo;
	
	@Depends("subfamily.number, subfamilyTo.number")
	public String getRangeDescription() { 		
		int subfamilyNumber = getSubfamily() == null?0:getSubfamily().getNumber();
		int subfamilyToNumber = getSubfamilyTo() == null?0:getSubfamilyTo().getNumber();
		return "FROM SUBFAMILY " + subfamilyNumber + " TO SUBFAMILY " + subfamilyToNumber;
	}

	public Subfamily2 getSubfamily() {
		return subfamily;
	}

	public void setSubfamily(Subfamily2 subfamily) {
		this.subfamily = subfamily;
	}

	public Subfamily2 getSubfamilyTo() {
		return subfamilyTo;
	}

	public void setSubfamilyTo(Subfamily2 subfamilyTo) {
		this.subfamilyTo = subfamilyTo;
	}
	
}
