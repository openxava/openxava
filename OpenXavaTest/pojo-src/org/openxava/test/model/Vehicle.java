package org.openxava.test.model;

import javax.persistence.*;
import javax.persistence.Entity;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@Views({
	@View(members="code; model; make; state, city"), 
	@View(name="Simple", members="code, model, make"),
	@View(name="Simplest", members="code, model"), 
	@View(name="WithGroupAndSection", members="code; model [model]; make { make }"),
	@View(name="ReadOnly", extendsView="WithGroupAndSection")
})
public class Vehicle {
	
	@Id @GeneratedValue(generator="system-uuid") @Hidden 
	@GenericGenerator(name="system-uuid", strategy="uuid")
	private String oid;
	
	@Column(length=5)
	@ReadOnly(forViews="ReadOnly") 
	private String code;
	
	@Column(length=40)
	@ReadOnly(forViews="ReadOnly") 
	private String model;
	
	@Column(length=20)
	@ReadOnly(forViews="ReadOnly") 
	private String make;

	/**
	 * DescriptionsList1 (key: state), DescriptionsList2 (key: state, city), Integer cityCode not in view.
	 * It not save the city value 
	 */
	@ManyToOne(fetch=FetchType.LAZY) @DescriptionsList
	@JoinColumn(name="STATE", referencedColumnName="ID")
	private State state;
	
	@LabelFormat(LabelFormatType.NO_LABEL)
	@Required
	@ManyToOne(fetch=FetchType.LAZY) 
	@DescriptionsList(depends="state.id", condition="${state.id} = ?")
	@JoinColumns({ 
		@JoinColumn(name="CITY", referencedColumnName="CODE", insertable=false, updatable=false),  
		@JoinColumn(name="STATE", referencedColumnName="STATE", insertable=false, updatable=false) 
	})	
	private City city;
	@Column(name="CITY")
	private Integer cityCode;
	
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getMake() {
		return make;
	}

	public void setMake(String make) {
		this.make = make;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
		this.cityCode = city == null ? new Integer(0) : new Integer(city.getCode());
	}

	public Integer getCityCode() {
		return cityCode;
	}

	public void setCityCode(Integer cityCode) {
		this.cityCode = cityCode;
	}


	
}
