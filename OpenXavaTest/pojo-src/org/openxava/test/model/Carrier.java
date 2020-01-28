package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@View(members="number; name; drivingLicence; warehouse; remarks; calculated; fellowCarriers; fellowCarriersCalculated")			
@View(name="Simple", members="number, name")
@View(name="CalculatedFellows", extendsView="Simple", members="; fellowCarriersCalculatedSize; fellowCarriersCalculated")
@View(name="ReadOnlyCalculatedFellows", members="number, name; fellowCarriersCalculated")
@View(name="FellowsNames", members="number, name; fellowCarriers")
@View(name="Fellows", members="number, name; warehouse; fellowCarriers") 
@View(
	name="WithSections",
	members=
		"number, name; fellowCarriersSelected; " +
		"data {drivingLicence; warehouse} " +
		"fellowCarriers {fellowCarriersCalculated} " )
@View(
	name="CollectionsTogether",
	members=
		"warehouse, oldSync; " +
		"fellowCarriers, fellowCarriersCalculated")	
@Tab(properties="calculated, number, name")
public class Carrier {
	
	@Id @Column(length=5)
	private int number;
	
	@Required @Column(length=40) @Stereotype("NO_FORMATING_STRING")	
	private String name;
	
	// We apply conversion (null into an empty String) to DRIVINGLICENCE_TYPE column
	// In order to do it, we create drivingLicence_level and drivingLicence_type
	// We make JoinColumns not insertable nor updatable, we modify the get/setDrivingLincence methods
	// and we create a drivingLicenceConversion() method.
	@DescriptionsList(notForViews="WithSections") // notForViews="WithSections" needed for testing a bug with the new renderer	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({ 
		@JoinColumn(name="DRIVINGLICENCE_LEVEL", referencedColumnName="LEVEL", insertable=false, updatable=false),  
		@JoinColumn(name="DRIVINGLICENCE_TYPE", referencedColumnName="TYPE", insertable=false, updatable=false) 
	})	
	private DrivingLicence drivingLicence;	
	private Integer drivingLicence_level; 
	private String drivingLicence_type; 
	
	@ManyToOne(optional=false) 
	@ReferenceView("KeyInGroup")
	@Action("WarehouseReference.createNewNoDialog") 
	@DescriptionsList(forViews="CollectionsTogether", descriptionProperties="name")
	private Warehouse warehouse;

	@Stereotype("MEMO")
	@DefaultValueCalculator(
		value=org.openxava.test.calculators.CarrierRemarksCalculator.class,
		properties={
			@PropertyValue(name="drivingLicenceType", from="drivingLicence.type") 
		}		
	)
	private String remarks;
	
	@Column(length=10) @Action("Carrier.translateName")
	public String getCalculated() {
		return "TR";
	}

	@CollectionView("Simple") @EditOnly
	@ListActions({
			@ListAction("Carrier.translateName"),
			@ListAction("Carrier.allToEnglish")
	})
	@Condition(
		"${warehouse.zoneNumber} = ${this.warehouse.zoneNumber} AND " + 
		"${warehouse.number} = ${this.warehouse.number} AND " +
		"NOT (${number} = ${this.number})"		
	)
	@OrderBy("number") 
	@ListProperties("number, name, remarks, calculated") 
	@Editor(forViews="FellowsNames", value="CarriersNames")
	public Collection<Carrier> getFellowCarriers() { 
		// At the moment you must write a code that returns the same result
		// of the @Condition. 
		if (getWarehouse() == null) return Collections.EMPTY_LIST; 
		Query query = XPersistence.getManager().createQuery("from Carrier c where " +
			"c.warehouse.zoneNumber = :zone AND " + 
			"c.warehouse.number = :warehouseNumber AND " + 
			"NOT (c.number = :number) " +
			"order by c.number");  
		query.setParameter("zone", getWarehouse().getZoneNumber());
		query.setParameter("warehouseNumber", getWarehouse().getNumber());
		query.setParameter("number",  getNumber());
		return query.getResultList();
	}

	@ReadOnly(forViews="ReadOnlyCalculatedFellows")
	@CollectionView("Simple")
	@RemoveSelectedAction(forViews="CalculatedFellows", value="")
	@ListAction("Carrier.translateName")
	@OnSelectElementAction(forViews="CalculatedFellows", value="Carrier.onSelectFellowCarriersCalculated") 
	@OnSelectElementAction(forViews="CollectionsTogether", value="Carrier.syncCarriersSelection")
	public Collection<Carrier> getFellowCarriersCalculated() {
		// This method exists for compliance with OpenXavaTest
		return getFellowCarriers();
	}
	
	@Transient
	public boolean oldSync;
	
	@Transient @ReadOnly
	private Integer fellowCarriersCalculatedSize;
	
	public static Collection<Carrier> findAll() {
		Query query = XPersistence.getManager().createQuery("from Carrier as o"); 
 		return query.getResultList();  				
	}	

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	public DrivingLicence getDrivingLicence() {
		// In this way because the column for type of driving lincence does not admit null
		try {
			if (drivingLicence != null) drivingLicence.toString(); // to force load
			return drivingLicence;
		}
		catch (EntityNotFoundException ex) {			
			return null;  
		}
	}

	public void setDrivingLicence(DrivingLicence licence) {
		// In this way because the column for type of driving lincence does not admit null
		this.drivingLicence = licence;
		this.drivingLicence_level = licence==null?null:licence.getLevel();		
		this.drivingLicence_type = licence==null?null:licence.getType(); 			
	}

	@PrePersist @PreUpdate
	private void drivingLicenceConversion() {
		if (this.drivingLicence_type == null) this.drivingLicence_type = "";
	}

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}


	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public void translate() {
		if (!translateToSpanish()) translateToEnglish();		
	}
	
	
	public boolean translateToEnglish() {
		// A naive implementation
		if ("UNO".equals(name)) {
			name = "ONE";
			return true;
		}
		if ("DOS".equals(name)) {
			name ="TWO";
			return true;
		}
		if ("TRES".equals(name)) {
			name ="THREE";
			return true;
		}
		if ("CUATRO".equals(name)) {
			name ="FOUR";
			return true;
		}		
		if ("CINCO".equals(name)) {
			name ="FIVE";
			return true;
		}
		return false;
	}
		
	public boolean translateToSpanish() {
		// A naive implementation
		if ("ONE".equals(name)) {
			name = "UNO";
			return true;
		}
		if ("TWO".equals(name)) {
			name ="DOS";
			return true;
		}
		if ("THREE".equals(name)) {
			name ="TRES";
			return true;
		}
		if ("FOUR".equals(name)) {
			name ="CUATRO";
			return true;
		}
		if ("FIVE".equals(name)) {
			name ="CINCO";
			return true;
		}
		return false;
	}	
	
	@Transient
	private String fellowCarriersSelected;

	public String getFellowCarriersSelected() {
		return fellowCarriersSelected;
	}

	public void setFellowCarriersSelected(String fellowCarriersSelected) {
		this.fellowCarriersSelected = fellowCarriersSelected;
	}

	public Integer getFellowCarriersCalculatedSize() {
		return fellowCarriersCalculatedSize;
	}

	public void setFellowCarriersCalculatedSize(Integer fellowCarriersCalculatedSize) {
		this.fellowCarriersCalculatedSize = fellowCarriersCalculatedSize;
	}

	public boolean isOldSync() {
		return oldSync;
	}

	public void setOldSync(boolean oldSync) {
		this.oldSync = oldSync;
	}
	
}