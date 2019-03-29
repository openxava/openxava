package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.test.actions.*;
import org.openxava.test.validators.*;
import org.openxava.util.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@EntityValidator(value=DeliveryPlaceValidator.class,
		properties = { @PropertyValue(name="customer") }
)
@Tab(properties="name, address, remarks, preferredWarehouse.name ")
public class DeliveryPlace {
	
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="CUSTOMER")
	private Customer customer;


	// It's calculAted in the method calculateOID
	@Id @Hidden 
	private int oid;
	
	@Column(length=30) @Required
	private String name;
	
	@Column(length=50) @Required
	private String address;
	
	@DescriptionsList
	@OnChange(OnChangePreferredWarehouseAction.class) 
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumns ({
		@JoinColumn(name="WAREHOUSE_ZONE", referencedColumnName="zone"),
		@JoinColumn(name="WAREHOUSE_NUMBER", referencedColumnName="number")
	})	
	private Warehouse preferredWarehouse;

	@Column(length=40)
	private String remarks;
	
	@OneToMany(mappedBy="deliveryPlace", cascade=CascadeType.ALL)
	private Collection<Receptionist> receptionists;
	
	@PrePersist
	private void calculateOID() {
		// Thus we can calculate an oid in a custom way

		// Generally its better to use the EJB3 standard techniques for
		// generating OIDs, but sometimes it's needed to execute custom
		// code, specially when you have to work with legate databases.
		try {
			NextIntegerCalculator calculator = new NextIntegerCalculator();
			calculator.setModel("Customer.DeliveryPlace");
			calculator.setProperty("oid");
			calculator.setConnectionProvider(DataSourceConnectionProvider.getByComponent("Customer"));
			this.oid = ((Number) calculator.calculate()).intValue();
		}
		catch (Exception ex) {
			throw new PersistenceException("Problem calculating OID of DeliveryPlace");
		}
	}


	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOid() {
		return oid;
	}

	public void setOid(int oid) {
		this.oid = oid;
	}

	public Warehouse getPreferredWarehouse() {
		return preferredWarehouse;
	}

	public void setPreferredWarehouse(Warehouse preferredWarehouse) {
		this.preferredWarehouse = preferredWarehouse;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Collection<Receptionist> getReceptionists() {
		return receptionists;
	}

	public void setReceptionists(Collection<Receptionist> receptionists) {
		this.receptionists = receptionists;
	}
	
}
