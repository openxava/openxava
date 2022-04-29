package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.View;
import org.openxava.annotations.Views;

/**
 * To test @javax.validation.constraints.Size with min and max elements on a 
 * collection of entities not embeddable.
 *
 * @author Jeromy Altuna
 */
@Entity
@Views({
	@View(extendsView="super.DEFAULT", 
		members="hounds { hounds }"),
	
	@View(name="NoHounds",
		extendsView="super.DEFAULT")
})
public class Hunter extends HunterAndHound {
	
	@OneToMany(mappedBy="hunter")
	@javax.validation.constraints.Size(min=1, max=2) 
	private Collection<Hound> hounds;
	
//	public Hunter() {
//		super();
//	}
//	
//	public Hunter(String name) {
//		this();
//		setName(name);
//	}
//	
//	public void addHound(Hound hound) {
//		if (hounds == null) hounds = new ArrayList<Hound>();
//		hound.setHunter(this);
//		hounds.add(hound);		
//	}
	
	public Collection<Hound> getHounds() {
		return hounds;
	}
	public void setHounds(Collection<Hound> hounds) {
		this.hounds = hounds;
	}	
}
