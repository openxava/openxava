package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Parameter;
import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.test.actions.*;
import org.openxava.test.calculators.*;
import org.openxava.test.validators.*;
import org.openxava.jpa.*;

/**
 * This is an example of using references as part of a composite key.<p>
 * 
 * In this case we combine @Id and @ManyToOne.<br>
 * 
 * @author Javier Paniza
 */

@Entity
@IdClass(DeliveryKey.class)
@EntityValidator(value=DeliveryValidator.class,
	properties = { @PropertyValue(name="invoice") }	
)
@Views({
	@View(members=
		"invoice;" +
		"deliveryData [" +
		"	type, number;" +			
		"	date;" +
		"	description;" +
		"	shipment;" +
		"	transportData [" +
		"		distance; vehicle; transportMode; driverType;" +
		"	]" +
		"	deliveryByData [" +
		"		deliveredBy;" +
		"		carrier;" +
		"		employee;" +			
		"	]" +
		"]"  +
		"comments { advice, shortcut; remarks }" + // shortcut in a section to test a case
		"incidents { incidents }" +
		"details {" +
		"	details [" +
		"		details" +
		"	]" +
		"}" 
	),
	@View(name="InInvoice", members="number; date; type; description;"),
	@View(name="GroupsInSections", members=
		"invoice;" +			
		"type, number;" +
		"deliveryData {" +							
		"	date;" +
		"	description;" +
		"	shipment;" +
		"	transportData [" +
		"		distance; vehicle; transportMode; driverType;" +
		"	]" +
		"	deliveryByData [" +
		"		carrier;" +
		"		employee;" +
		"	]" +			
		"}" +
		"remarks { remarks }" +
		"incidents { incidents }" +
		"details { details }" 
	),
	@View(name="MoreSections", members=
		"invoice;" +			
		"deliveryData ["  +
		"	type, number;" +			
		"	date;" +
		"	description;" +				
		"]" +
		"transportData {" +
		"	distance; vehicle; transportMode; driverType;" +
		"}" +
		"deliveryByData {" +
		"	deliveredBy;" +
		"	carrier;" +
		"	employee;" +			
		"}" +
		"incidents { incidents }" +			
		"remarks { advice, shortcut; remarks }" +			
		"details {" +
		"	details" +
		"}"					
	),
	@View(name="Simple", members=
		"invoice;" +			
		"deliveryData ["  +
		"	type, number;" +			
		"	date;" +
		"	description;" +				
		"]"
	),
	@View(name="FullInvoice", members= "invoice; number; description"),
	@View(name="Search", members= "invoice; type; number; date;	description;")
})
@Tabs({
	@Tab(properties="invoice.year, invoice.number, type.number, type.description, number, date, description, distance, dateAsLabel, invoice.sellerDiscount, invoice.amountsSum, invoice.total, invoice.date"), 
	@Tab(name="Remarks2002", 
		properties="invoice.year, invoice.number, remarks",
		baseCondition="${invoice.year} = 2002"
	),
})


public class Delivery {
	
	@Id @Column(length=5)
	@Action(forViews="DEFAULT, MoreSections", value="Delivery.generateNumber")
	@Action(forViews="DEFAULT", value="Delivery.generateNumber88")
	private int number;

	
	@Id @ManyToOne(fetch=FetchType.LAZY)
	@OnChange(forViews="DEFAULT, MoreSections", value=OnChangeInvoiceNumberInDeliveryAction.class)
	@ReferenceView(notForViews="FullInvoice", value="Simple") 
	@NoFrame(forViews="FullInvoice") 
	@Action(forViews="DEFAULT, MoreSections", value="Delivery.setDefaultInvoice")
	private Invoice invoice;
	
	// JoinColumn and ManyToOne fetch are also specified in DeliveryKey because 
	// a bug in Hibernate, see http://opensource.atlassian.com/projects/hibernate/browse/ANN-361
	// We use code in 'getType()' for simulate a hibernate not-found='ignore'
	@Id @ManyToOne(fetch=FetchType.LAZY)	
	@JoinColumn(name="TYPE")
	@DescriptionsLists({		
		@DescriptionsList(forViews="DEFAULT, MoreSections, Search, Simple", order="${number} desc"), 
		@DescriptionsList(forViews="GroupsInSections")
	})
	@Action(forViews="DEFAULT, MoreSections", value="Delivery.setDefaultType")
	@OnChange(forViews="DEFAULT", value = OnChangeDeliveryTypeAction.class) 
	private DeliveryType type;
	
	@Type(type="org.openxava.types.Date3Type") 
	@Columns(columns = { @Column(name="year"), @Column(name="month"), @Column(name="day") })
	@Required
	@DefaultValueCalculator(CurrentDateCalculator.class)
	private Date date;
	
	private String description;

	
	@Type(type="org.openxava.types.EnumLetterType", 
		parameters={
			@Parameter(name="letters", value="LNI"), 
			@Parameter(name="enumType", value="org.openxava.test.model.Delivery$Distance")
		}
	)
	@OnChange(forViews="DEFAULT, MoreSections", value=OnChangeDistanceAction.class)
	private Distance distance;
	public enum Distance { LOCAL, NATIONAL, INTERNATIONAL };
	
	@Column(length=15)
	@OnChange(forViews="DEFAULT, MoreSections", value=OnChangeVehicleAction.class)
	@OnChange(forViews="GroupsInSections", value=OnChangeAddMessageAction.class)
	private String vehicle;
	
	@Column(length=20)
	private String driverType;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="CARRIER")
	@DescriptionsList(notForViews="GroupsInSections")
	@OnChange(forViews="DEFAULT, MoreSections", value=OnChangeCarrierInDeliveryAction.class)
	private Carrier carrier;
	
	
	private String employee;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@DescriptionsList(notForViews="GroupsInSections")
	private Shipment shipment;
	
	@Stereotype("MEMO") @Column(length=500)
	private String remarks;
	
	@Stereotype("MEMO") @Column(length=500)
	private String incidents;	
	
	@OneToMany (mappedBy="delivery", cascade=CascadeType.REMOVE)
	@Size(max=3) 
	@NewAction(forViews="DEFAULT, MoreSections", value="DeliveryDetail.new")
	@SaveAction(forViews="DEFAULT, MoreSections", value="DeliveryDetail.save")
	@DetailAction(forViews="DEFAULT, MoreSections", value="DeliveryDetail.saveFailing") 
	@HideDetailAction(forViews="DEFAULT, MoreSections", value="DeliveryDetail.hideDetail")
	@RemoveAction(forViews="DEFAULT, MoreSections", value="DeliveryDetail.remove")
	@RemoveSelectedAction(forViews="DEFAULT, MoreSections", value="DeliveryDetail.removeSelected")
	private Collection<DeliveryDetail> details;	
	
	@Transient
	// A calculator that return null is like not have it, 
	// but we set for verifying that we can have a calculator and not fail
	@DefaultValueCalculator(value=EnumCalculator.class,
		properties={
			@PropertyValue(name="enumType", value="org.openxava.test.model.Delivery$DeliveredBy")
			// If we do not put value EnumCalculator will return null
			//@PropertyValue(name="value", value="CARRIER")
		}
	)
	@OnChange(OnChangeDeliveryByAction.class)
	private DeliveredBy deliveredBy;
	public enum DeliveredBy { EMPLOYEE, CARRIER }
	
	@Transient
	@Column(length=40) @Required
	@DefaultValueCalculator(AdviceCalculator.class)
	private String advice;
	
	@Transient
	@Column(length=2)
	@OnChange(OnChangeShortcutAction.class)
	private String shortcut;
		
	@Stereotype("LABEL")
	public Date getDateAsLabel() {
		return date;
	}

	@Column(length=20) @Stereotype("LABEL") @Depends("vehicle")
	public String getTransportMode() {
		if ("PLANE".equalsIgnoreCase(vehicle)) return "AIR";
		if ("MOTORBIKE".equalsIgnoreCase(vehicle)) return "STREET/ROAD";		
		if ("CAR".equalsIgnoreCase(vehicle)) return "HIGHWAY";
		return "";				
	}
	
	public static Collection<Delivery> findAll() {
		Query query = XPersistence.getManager().createQuery("from Delivery as o"); 
 		return query.getResultList();  				
	}	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Invoice getInvoice() {
		return invoice;
	}

	public void setInvoice(Invoice invoice) {
		this.invoice = invoice;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public DeliveryType getType() {
		if (type != null) { 
			// We simulate programmatically a hibernate not-found='ignore'
			try {
				type.toString();
			}
			catch (Exception ex) {
				return null;
			}
		}			
		return type;
	}

	public void setType(DeliveryType type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Distance getDistance() {
		return distance;
	}

	public void setDistance(Distance distance) {
		this.distance = distance;
	}

	public String getVehicle() {
		return vehicle;
	}

	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}

	public String getDriverType() {
		return driverType;
	}

	public void setDriverType(String driverType) {
		this.driverType = driverType;
	}

	public Carrier getCarrier() {
		return carrier;
	}

	public void setCarrier(Carrier carrier) {
		this.carrier = carrier;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public Shipment getShipment() {
		return shipment;
	}

	public void setShipment(Shipment shipment) {
		this.shipment = shipment;
	}

	public String getIncidents() {
		return incidents;
	}

	public void setIncidents(String incidents) {
		this.incidents = incidents;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	
	public Collection<DeliveryDetail> getDetails() {
		return details;
	}

	public void setDetails(Collection<DeliveryDetail> details) {
		this.details = details;
	}
	

	public DeliveredBy getDeliveredBy() {
		return deliveredBy;
	}

	public void setDeliveredBy(DeliveredBy deliveredBy) {
		this.deliveredBy = deliveredBy;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public String getShortcut() {
		return shortcut;
	}

	public void setShortcut(String shortcut) {
		this.shortcut = shortcut;
	}
		
}
