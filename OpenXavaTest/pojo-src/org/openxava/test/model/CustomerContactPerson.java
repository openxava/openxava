package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * This is only for testing a single reference as key. <br>
 * 
 * A way to achieve to use a single reference as key is:
 * <ul> 
 * <li> Define the reference as @ManyToOne or @OneToOne and @Id
 * <li> Define the entity as serializable.
 * </ul> 
 * 
 * @author Javier Paniza
 */

@Entity 
@Views({
	@View(name="CustomerAsAggregate3Levels"), 
	@View(name="Simple", members="name; customer")
})
public class CustomerContactPerson implements java.io.Serializable {
		
	@ReferenceView(value="Simple")
	@ReferenceViews({
		@ReferenceView(forViews="Simple", value="Simplest"),
		@ReferenceView(forViews="CustomerAsAggregate3Levels", value="SellerAsAggregate2Levels")		
	})		
	@AsEmbedded(forViews="CustomerAsAggregate3Levels")	 
	@Id @OneToOne(fetch=FetchType.LAZY) 
	private Customer customer;
	
	@Stereotype("PERSON_NAME") @Required
	private String name;
	
	
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
