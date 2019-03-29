package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.validators.*;

/**
 * 
 * @author Javier Paniza
 */
@Entity
@View(members="number; description; comboDeliveries")
@RemoveValidator(value=DeliveryTypeRemoveValidator.class,
	properties=@PropertyValue(name="number")
)
public class DeliveryType {
	
	@Id @Column(length=4)
	private int number;
	
	@Required
	private String description;
	
	@OneToMany(mappedBy="type")
	private Collection<Delivery> deliveries;

	// For testing description lists which keys	are references to other entities	
	@Transient @Stereotype("DELIVERIES")
	private Delivery comboDeliveries;	

	@PrePersist
	private void prePersist() {
		setDescription(getDescription() + " CREATED");
	}
	
	@PreUpdate
	private void preUpdate() {
		setDescription(getDescription() + " MODIFIED");
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Collection<Delivery> getDeliveries() {
		return deliveries;
	}

	public void setDeliveries(Collection<Delivery> deliveries) {
		this.deliveries = deliveries;
	}

	public Delivery getComboDeliveries() {
		return comboDeliveries;
	}

	public void setComboDeliveries(Delivery comboDeliveries) {
		this.comboDeliveries = comboDeliveries;
	}

}
