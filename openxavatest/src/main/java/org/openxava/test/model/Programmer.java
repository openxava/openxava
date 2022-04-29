package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@DiscriminatorValue("PRO")
@Views({
	@View(name="Simplest", members="name"),
	@View(name="WithSections",
		members = 
			"name, sex;" +
			"mainLanguage;" +
			"experiences { experiences }"
	),
	@View(name="WithGroupInSection",
		members = 
			"name;" +
			"data {" +
				"experiences;" +	
				"personal [ " +
				"	sex; mainLanguage;" +
				"]" +				
			"}"
	)
})
public class Programmer extends Human {

	@Column(length=20)
	private String mainLanguage;
		
	@OneToMany(mappedBy="programmer", cascade=CascadeType.REMOVE)
	private Collection<Experience> experiences;

	public String getMainLanguage() {
		return mainLanguage;
	}

	public void setMainLanguage(String mainLanguage) {
		this.mainLanguage = mainLanguage;
	}

	public Collection<Experience> getExperiences() {
		return experiences;
	}

	public void setExperiences(Collection<Experience> experiences) {
		this.experiences = experiences;
	}
	
}
