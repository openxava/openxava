package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.actions.*;

/**
 * Create on 16/01/2012 (09:54:11)
 * @author Ana Andres
 * @author Javier Paniza
 */
@IdClass(CityKey.class)
@Entity
@Tab(properties="code, name, state.fullNameWithFormula")
// tmp @View(members="state, stateCondition; code; name; location")
@View(members=
	"city [ state; "
		+ "stateCondition;"
		+ "code;"
		+ "name;"
		+ "population;"
		+ "zipCode;"
		+ "county;"
		+ "country;"
		+ "settled;"
		+ "area;"
		+ "elevation;"
		+ "governmentType;"
		+ "mayor;"
	+ "], "
	+ "location")
public class City {
	
	@Id 
	@ManyToOne(fetch=FetchType.LAZY) 
	@DescriptionsList
	@JoinColumn(name="STATE", referencedColumnName="ID")
	private State state;
	
	@Id
	private int code;
	
	@Stereotype("CITY_NAME")
	private String name;

	@Transient
	@OnChange(OnChangeStateConditionInCity.class)
	private String stateCondition;
	
	@Stereotype("COORDINATES") @Column(length=50)
	private String location;
	
	@Transient
	private int population; // tmp
	
	@Transient @Column(length=5)
	private String zipCode; // tmp
	
	@Transient @Column(length=50)
	private String county; // tmp
	
	@Transient @Column(length=50)
	private String country; // tmp 
	
	@Transient @Column(length=4)
	private int settled; // tmp
	
	@Transient 
	private int area; // tmp
	
	@Transient
	private int elevation; // tmp
	
	@Transient @Column(length=50)
	private String governmentType; // tmp
	
	@Transient @Column(length=50)
	private String mayor; // tmp	
		
	public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
		this.population = population;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public int getSettled() {
		return settled;
	}

	public void setSettled(int settled) {
		this.settled = settled;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStateCondition() {
		return stateCondition;
	}

	public void setStateCondition(String stateCondition) {
		this.stateCondition = stateCondition;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public int getElevation() {
		return elevation;
	}

	public void setElevation(int elevation) {
		this.elevation = elevation;
	}

	public String getGovernmentType() {
		return governmentType;
	}

	public void setGovernmentType(String governmentType) {
		this.governmentType = governmentType;
	}

	public String getMayor() {
		return mayor;
	}

	public void setMayor(String mayor) {
		this.mayor = mayor;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
