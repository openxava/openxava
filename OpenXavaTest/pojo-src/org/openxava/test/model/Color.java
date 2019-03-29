package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.test.actions.*;

/**
 * Number (the key) can be 0
 *  
 * @author Javier Paniza
 */

@Entity
@Views({
	@View( name="Ordinary", members="number; name; sample; hexValue; usedTo, characteristicThing, anotherCT; mixture"),
	@View( name="Ordinary2", members="number; name; sample; hexValue; usedTo, characteristicThing"),	
	@View( name="View1", members="property1"), 
	@View( name="View2", members="property2"), 
	@View( name="View2Sub1", members="property2Sub1"), 
	@View( name="View2Sub2", members="property2Sub2"),
	@View( name="Groups", members="group; group1[property1], group2[property2]"),
	@View( name="Sub", members="actionNumber")
})
@Tab( properties = "number, name, hexValue, sample, usedTo.name, characteristicThing.description")
@Tab( name="Color2", properties = "number, name, hexValue, sample, usedTo.name, characteristicThing.description")	
public class Color {

	@Id @Column(length=5)
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@LabelStyle("reverse-label")
	@LabelStyle("italic-label")	
	private Integer number;
	
	@Column(length=20) @Required
	@LabelStyle(value="bold-label", notForViews="Ordinary2")
	@LabelFormat(LabelFormatType.SMALL)
	private String name;
	
	@Version 
	private Integer version;
	
	@Column(length=6)
	private String hexValue; 
	
	@Stereotype("IMAGE_LABEL")
	public String getSample() {
		if ("red".equalsIgnoreCase(name) ||
			"rojo".equalsIgnoreCase(name)) return "RED";
		if ("black".equalsIgnoreCase(name) ||
			"negro".equalsIgnoreCase(name)) return "BLACK";
		return "nocolor";		
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(notForTabs="DEFAULT", order="${name} desc") 
	@JoinColumn(name="IDTHING")
	@LabelStyle("italic-label")
	private Thing usedTo;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(
		condition="${thing.number} = ?", 
		depends="this.usedTo")
	@DescriptionsList(
		forViews="Ordinary2", 
		condition="${number} < 2",
		forTabs="Color2")
	@LabelFormat(LabelFormatType.NO_LABEL)
	private CharacteristicThing characteristicThing;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({ 
		@JoinColumn(name="MIXTURE_COLORNAME1", referencedColumnName="COLORNAME1"),
		@JoinColumn(name="MIXTURE_COLORNAME2", referencedColumnName="COLORNAME2"),
	})
	@DescriptionsList(descriptionProperties="fullDescription")
	private Mixture mixture;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(	
		// This is for testing the ${Thing} is well interpreted, though since v4.5 you can use just Thing
		// because condition uses JPA query syntax
		condition="${thing.number} = (SELECT number FROM ${Thing} WHERE name = 'CAR')"
		
		// It does not works because IDTHING is a column name, so JPA does not recognized it
		// condition="${thing.number} = (SELECT IDTHING FROM ${Thing} WHERE name = 'CAR')" 

		// We can use a pure JPQL query without ${propertyName}
		// condition="e.thing.number = (SELECT t.number FROM Thing t WHERE t.name = 'CAR')" 		 
	)
	private CharacteristicThing anotherCT; // to test the change model name by table name: ${Thing} -> THING  
	
	@Transient
	private String property1;
	
	@Transient
	private String property2;
	
	@Transient
	private String property2Sub1;
	
	@Transient
	private String property2Sub2;
	
	@Transient
	@OnChange(forViews="Groups", value=OnChangeGroupInColorAction.class) 
	private Group group;
	public enum Group { GROUP1, GROUP2 }
	
	@Transient
	public int actionNumber;
	
	public static Collection<Color> findAll() {
		Query query = XPersistence.getManager().createQuery("from Color");
		return query.getResultList();
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getProperty1() {
		return property1;
	}

	public void setProperty1(String property1) {
		this.property1 = property1;
	}

	public String getProperty2() {
		return property2;
	}

	public void setProperty2(String property2) {
		this.property2 = property2;
	}

	public String getProperty2Sub1() {
		return property2Sub1;
	}

	public void setProperty2Sub1(String property2Sub1) {
		this.property2Sub1 = property2Sub1;
	}

	public String getProperty2Sub2() {
		return property2Sub2;
	}

	public void setProperty2Sub2(String property2Sub2) {
		this.property2Sub2 = property2Sub2;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public String getHexValue() {
		return hexValue;
	}

	public void setHexValue(String hexValue) {
		this.hexValue = hexValue;
	}

	public Thing getUsedTo() {
		return usedTo;
	}

	public void setUsedTo(Thing usedTo) {
		this.usedTo = usedTo;
	}

	public CharacteristicThing getCharacteristicThing() {
		return characteristicThing;
	}

	public void setCharacteristicThing(CharacteristicThing characteristicThing) {
		this.characteristicThing = characteristicThing;
	}

	public CharacteristicThing getAnotherCT() {
		return anotherCT;
	}

	public void setAnotherCT(CharacteristicThing anotherCT) {
		this.anotherCT = anotherCT;
	}

	public Mixture getMixture() {
		return mixture;
	}

	public void setMixture(Mixture mixture) {
		this.mixture = mixture;
	}

	public int getActionNumber() {
		return actionNumber;
	}

	public void setActionNumber(int actionNumber) {
		this.actionNumber = actionNumber;
	}

}
