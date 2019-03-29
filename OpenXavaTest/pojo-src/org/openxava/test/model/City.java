package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.actions.*;

/**
 * Create on 16/01/2012 (09:54:11)
 * @author Ana Andres
 */
@IdClass(CityKey.class)
@Entity
@Tab(properties="code, name, state.fullNameWithFormula")
@View(members="state, stateCondition; code; name")
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

}
