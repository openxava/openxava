package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@DiscriminatorValue("JPR")
@Tab(baseCondition="${mainLanguage} = 'JAVA'")
@Views({
	@View(members="name, sex; mainLanguage, favouriteFramework; experiences"),	
	@View(name="WithSections", extendsView="super.WithSections", 
		members = 
			"favouriteFramework;" +
			"frameworks { frameworks }"
	), 
	@View(name="Complete", extendsView="DEFAULT", 
		members = "frameworks"
	),
	@View(name="Simplest", extendsView="super.Simplest", members="favouriteFramework"),
	@View(name="VerySimple", members="name, sex"),
	@View(name="Simple", extendsView="VerySimple",
		members="mainLanguage"),
	@View(name="WithSectionsAsProgrammer", extendsView="super.WithSections"),
	@View(name="WithSectionsNoFavouriteFramework", extendsView="super.WithSections", 
		members = "frameworks { frameworks }"
	),
	@View(name="WithGroupInSection", extendsView="super.WithGroupInSection", 
		members = 
			"favouriteFramework;" +
			"frameworks { frameworks }"
	)
})
public class JavaProgrammer extends Programmer {

	@Column(length=20)	
	private String favouriteFramework;
	
	@OneToMany(mappedBy="javaProgrammer")
	private Collection<Framework> frameworks; 

	public String getFavouriteFramework() {
		return favouriteFramework;
	}

	public void setFavouriteFramework(String favouriteFramework) {
		this.favouriteFramework = favouriteFramework;
	}
	
	public Collection<Framework> getFrameworks() {
		return frameworks;
	}

	public void setFrameworks(Collection<Framework> frameworks) {
		this.frameworks = frameworks;
	}
		
}
