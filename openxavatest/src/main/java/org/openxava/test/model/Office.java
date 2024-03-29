package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * Example of reference with a member shared with a property. <p>
 * 
 * For this we have to put @JoinColumn..., insertable=false, updatable=false)
 * in the all the columns of the reference, the overlapped ones and the not overlapped
 * ones. Also you need to map the not overlapped column as simple fields too, and assign
 * values to them in the set of the reference.<br>
 * In this case the property and the reference are not keys.<br>
 *  
 * @author Javier Paniza
 */

@Entity
@Views({
	@View(members=
		"number;" +
		"data {" +
		"	zoneNumber;" +
		"	name;" +
		"	mainWarehouse;" +
		"	officeManager;" +
		"	defaultCarrier;" +
		"	receptionist;" +
		"}"
	),
	@View(name="OnlyWarehouse", members="number; zoneNumber; mainWarehouse"),
	@View(name="WithDescriptionsLists", members="number; zoneNumber; name; mainWarehouse; defaultCarrier"),
	@View(name="WithDescriptionsListsWithReferenceInCondition", extendsView="WithDescriptionsLists")
})
@Tab(properties="zoneNumber, number, name, mainWarehouse.name, officeManager.name, defaultCarrier.name")
public class Office {

	@Id @Required @Column(length=3)
	private int number;
	
	@Required @Column(length=3, name="ZONE")
	private int zoneNumber;
	
	@Required @Column(length=40)
	private String name;
		
	@ManyToOne(optional=false, fetch=FetchType.LAZY) 
	@JoinColumns({ 
		@JoinColumn(name="ZONE", referencedColumnName="ZONE", insertable=false, updatable=false),  
		@JoinColumn(name="WAREHOUSE_NUMBER", referencedColumnName="NUMBER", insertable=false, updatable=false) 
	})
	@ReferenceView(forViews="OnlyWarehouse", value="WithoutZone") 
	@DescriptionsList(forViews="WithDescriptionsLists, WithDescriptionsListsWithReferenceInCondition") 
	private Warehouse mainWarehouse;
	@Column(name="WAREHOUSE_NUMBER")
	private Integer mainWarehouse_number; 
	
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumns({
		@JoinColumn(name="ZONE", referencedColumnName="ZONE", insertable=false, updatable=false), 
		@JoinColumn(name="NUMBER", referencedColumnName="OFFICE", insertable=false, updatable=false), 
		@JoinColumn(name="MANAGER_NUMBER", referencedColumnName="NUMBER", insertable=false, updatable=false)
	})
	private Clerk officeManager;
	@Column(name="MANAGER_NUMBER")
	private Integer officeManager_number;
	
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumn(name="CARRIER_NUMBER")
	@DescriptionsList(forViews="WithDescriptionsLists", depends = "mainWarehouse", condition="${warehouse.zoneNumber} = ? and ${warehouse.number} = ?") 
	@DescriptionsList(forViews="WithDescriptionsListsWithReferenceInCondition", depends = "mainWarehouse", condition="${warehouse} = ?")  
	private Carrier defaultCarrier;
	
	@Stereotype("RECEPTIONIST") @Column(name="RECEPTIONIST_OID")
	private int receptionist;
	
	
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getZoneNumber() {
		return zoneNumber;
	}

	public void setZoneNumber(int zoneNumber) {
		this.zoneNumber = zoneNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Warehouse getMainWarehouse() {
		return mainWarehouse;
	}

	public void setMainWarehouse(Warehouse mainWarehouse) {
		this.mainWarehouse = mainWarehouse;
		this.mainWarehouse_number = mainWarehouse == null?null:mainWarehouse.getNumber();
	}

	public Clerk getOfficeManager() {
		return officeManager;
	}

	public void setOfficeManager(Clerk officeManager) {
		this.officeManager = officeManager;
		this.officeManager_number = officeManager==null?null:officeManager.getNumber();
	}

	public Carrier getDefaultCarrier() {
		return defaultCarrier;
	}

	public void setDefaultCarrier(Carrier defaultCarrier) {
		this.defaultCarrier = defaultCarrier;
	}

	public int getReceptionist() {
		return receptionist;
	}

	public void setReceptionist(int receptionist) {
		this.receptionist = receptionist;
	}
	
}
