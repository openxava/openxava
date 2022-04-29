package org.openxava.test.model;

import org.openxava.test.validators.*;
import org.openxava.annotations.*;
import javax.persistence.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity 
@EntityValidators ({ 
	@EntityValidator(value=PhoneNumberValidator.class, 
		properties={  
			@PropertyValue(name="phoneCountry"), 
			@PropertyValue(name="phone") 
	}
	) 
})   
public class Phone {
	 
	@Id @Hidden @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(length = 8) 
	private int phoneId; 
	
	@ManyToOne(optional = false) @NoCreate @NoModify 
	@DescriptionsList
	private Country phoneCountry; 
	
	@Column(length = 20) @Required 
	private String phone; // This must name like the entity in order to test a case
	 	
	private int phoneExtension;

	public int getPhoneId() {
		return phoneId;
	}

	public void setPhoneId(int phoneId) {
		this.phoneId = phoneId;
	}

	public Country getPhoneCountry() {
		return phoneCountry;
	}

	public void setPhoneCountry(Country phoneCountry) {
		this.phoneCountry = phoneCountry;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getPhoneExtension() {
		return phoneExtension;
	}

	public void setPhoneExtension(int phoneExtension) {
		this.phoneExtension = phoneExtension;
	}
	
} 