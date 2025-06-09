package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.test.actions.*;


/**
 * 
 * @author Javier Paniza
 */

@Entity

@View( members= 	
	"number;" +  
	"type;" +
	"name, Customer.changeNameLabel();" +
	"photo;" +
	"telephone, email;" +
	"website;" +
	"address;" +
	"city;" +		
	"seller [" +  
	"	seller; " +
	"	relationWithSeller;" +
	"]" +
	"alternateSeller;" +		
	"deliveryPlaces;" +
	"remarks" 
)

@View(name = "FramesOnSameRow", members= 	
	"number;" +
	"type;" +
	"name, Customer.changeNameLabel();" +		
	"photo;" +
	"telephone, email;" +
	"website;" +
	"address;" +
	"city;" +
	"seller [" +  
	"	seller; " +
	"	relationWithSeller;" +
	"]," + //<--- comma makes frames/groups to be on the same row				
	"alternateSeller;" +		
	"deliveryPlaces;" +
	"remarks" 	
)

@View( name="GroupAndPropertySameRow", members=
	"number [ number ] " + // Without ;
	"name"
)

@View(name = "ThreeFramesOnSameRow", members= 	
	"number;" +
	"type;" +
	"name, Customer.changeNameLabel();" +		
	"photo;" +
	"telephone, email;" +
	"website;" +
	"city;" +
	"address," + //<--- comma makes frames/groups to be on the same row
	"seller [" +  
	"	seller; " +
	"	relationWithSeller;" +
	"]," + //<--- comma makes frames/groups to be on the same row				
	"alternateSeller;" +		
	"deliveryPlaces;" +
	"remarks" 	
)

@View(name = "FramesAndPropertyOnSameRow", members= 	
	"number;" +
	"type;" +
	"name, Customer.changeNameLabel();" +		
	"photo;" +
	"telephone, email;" +
	"website;" +
	"address;" +
	"seller [" +  
	"	seller; " +
	"	relationWithSeller;" +
	"]," + //<--- comma makes frames/groups to be on the same row
	"city," + //<--- comma makes frames/groups to be on the same row				
	"alternateSeller;" +		
	"deliveryPlaces;" +
	"remarks" 	
)

@View( name="Simple", members=
	"number;" +
	"type;" +
	"name, Customer.changeNameLabel(ALWAYS);" +		
	"photo;" +
	"address;"
)

@View( name="SimpleWithCity", extendsView="Simple", members= "; city") 

@View( name="Simplest", members="number; name" )

@View( name="Minimum", members="number; name; type" ) 

@View( name="WithSection", members=	
	"number;" + 
	"customer {" + 				
	"	type;" + 
	"passport, creditCard;" + 
	"	name, Customer.changeNameLabel();" +
	"	photo;" +
	"	telephone, email, additionalEmails;" + 
	"	website;" +		
	"	address;" +
	"	city, extendedCity;" + 
	"	seller [" + 
	"		seller;" +
	"		relationWithSeller;" + 
	"	]" +   
	"	alternateSeller;" +
	"   transientSeller;" + 	
	"	deliveryPlaces;" +
	"	remarks;" + 	
	"}" + 
	"states { states }"			
)

@View( name="ManySections", members=	
	"number;" + 
	"type { type; }" + 
	"name { name, Customer.changeNameLabel(); }" +
	"photo { photo; }" +
	"contacts {	telephone, email, additionalEmails; }" + 
	"websites {	website; }" +		
	"address { address; }" + 
	"city { city; }" +
	"seller {" + 
	"	seller;" +
	"	relationWithSeller;" + 
	"}" +   
	"alternateSeller { alternateSeller; }" + 
	"deliveryPlaces { deliveryPlaces; }" +
	"remarks { remarks; }" +
	"states { states }"			
)

@View( name="SectionGroup", members=	
	"number;" +		
	"data {" + // Only one section to test a case
	"	customer [" +
	"		type;" +
	"		name;" + 
	"	]" +
	"	seller [" +
	"		seller;" +
	"		relationWithSeller;" +
	"	]" +
	"}"
)

@View( name="SimpleStateAsForm", members= "number; type; name; address" )

@View( name="SellerAsAggregate", members= "number; type; name; address; seller" )

@View( name="SomeMembersReadOnly", members= "number; type; name; seller; alternateSeller" )

@View( name="NameReadOnlyInGroup", members= "number; data [ type; name ]" )

@View( name="SimpleWithDeliveryPlaces", members="number; type; name; address; deliveryPlaces" )

@View( name="SellerAsAggregate2Levels", members="number; type; name; address; seller" )

@View( name="TypeWithRadioButton", members=	"number; data [	type; name;	address; ]" )

@View( name="Demo", members="number; type; name; photo; address; seller")

@View( name="NotAligned", members="number, type, name; telephone, email, website")

@View( name="Aligned", members="#number, type, name; telephone, email, website")

@View( name="Intermediate", members="number")

@View( name="SellerAsDescriptionsListShowingReferenceView", members= "number; name; type; seller" )
	
@View( name="SellerAsDescriptionsListShowingReferenceViewNoKey", members= "number; name; type; seller" )

@View( name="SellerAsDescriptionsListShowingReferenceViewNoFrameInSection", members= "number; name; type; seller { seller }" )

@View( name="OnlyAddress", members= "address" ) // Don't add more members

@View( name="WithGroups", members=
	"general [number;"  
		+ "type;"
		+ "name, Customer.changeNameLabel()"
	+ "];"
	+ "data [photo;"
		+ "telephone, email;"
		+ "website;"
		+ "address;"
		+ "city"
	+ "];"		
	+ "seller ["  
		+ "seller; "
		+ "relationWithSeller;"
		+ "alternateSeller;"
	+ "];"
	+ "other ["
		+ "deliveryPlaces;" 
		+ "remarks"
	+ "]" 
)

@View( name="SellerInSection", members= 	
	"number;" + 
	"type;" +
	"name;" +
	"seller {" +
	"	seller; " +
	"}"
)

@View( name="PersonalInformation", members="name; passport, creditCard;")

@View( name="OnlyAddress", members="address;") // ; at end to test a case  

@View( name="SearchListCondition", members="number; name; sellerNameInCondition; seller; transientSeller; alternateSeller;")

@Tabs ({
	@Tab(
		rowStyles={
			@RowStyle(style="row-highlight", property="type", value="steady"),
			@RowStyle(style="italic-label", property="type", value="steady"),
			@RowStyle(style="row-red", property="type", value="special")	// 'row-red' defined in file 'custom.css'
		},
		properties="name, type, seller.name, address.city, seller.level.description, address.state.name, website" 
	),	
	@Tab( name="TwoSellers",
		properties="name, type, address.city, seller.name, seller.level.description, alternateSeller.name"		
	),	
	@Tab( name="TwoSellersNumber",
		properties="name, type, seller.number, alternateSeller.number"
	),
	@Tab ( name ="Cards", editor="CustomerCardList", 
		properties="number, name, type, address.city, address.state.name"
	),
	@Tab ( name ="WithCards", editors ="List, Charts, CustomerCardList, NotExist", 
		properties="number, name, type, address.city, address.state.name"
	),
	@Tab ( name ="OnlyCards", editors ="CustomerCardList", 
		properties="number, name, type, address.city, address.state.name"
	),
	@Tab( name="Demo", 
		properties="name, type, seller.name"
	),
	@Tab(name="FromAlaska",  
		properties="number, name", 
		baseCondition="from Customer e, in (e.states) s where s.id = 'AK'"),
	@Tab(name="LessThanFour",
		properties="number, name", 
		baseCondition="${number} < 4")
})
public class Customer implements IWithName {
	
	@Id @Column(length=5)	
	private int number;
		
	@Required @Stereotype("PERSON_NAME")  
	@OnChange(OnChangeCustomerNameAction.class)
	@ReadOnly(forViews="SomeMembersReadOnly, NameReadOnlyInGroup")
	@Editor(forViews="SimpleStateAsForm", value="JbyXName")
	private String name;
	
	@Required 
	@DefaultValueCalculator(
		value=org.openxava.calculators.IntegerCalculator.class,
		properties={ @PropertyValue(name="value", value="2") }		
	)
	@Editor(forViews="TypeWithRadioButton", value="ValidValuesRadioButton")
	@Column(length=1) // Required for a test case 
	private Type type;
	public enum Type { NORMAL, STEADY, SPECIAL };
	
	@OnChange(forViews="FramesOnSameRow", value=OnChangePhotoAction.class) 
	@Stereotype("PHOTO")
	private byte [] photo;
	
	@Telephone 
	private String telephone;
	
	@Stereotype("EMAIL") 
	@DisplaySize(130) 
	@Column(length = 254)
	private String email;
	
	@EmailList 
	@DisplaySize(50)  
	private String additionalEmails;
	
	@Stereotype("WEBURL") @Column(length=100)
	@Editor(forViews="WithSection", value="CustomWebURL") 
	private String website;
		
	@Stereotype("MEMO") @Column(length=400)	
	private String remarks;
		
	@Embedded
	@ReferenceView(forViews="SimpleStateAsForm", value="StateAsForm")
	@ReferenceView(forViews="Demo", value="Demo")
	@ReferenceView(forViews="OnlyAddress", value="Demo") 
	private Address address;	
	
	@Transient @Column(length=50)
	private String extendedCity; 
	
	@ManyToOne(fetch=FetchType.LAZY) @SearchAction("MyReference.search")
	@ReadOnly(forViews="SomeMembersReadOnly")
	@AsEmbedded(forViews="SellerAsAggregate, SellerAsAggregate2Levels")
	@NoFrame(notForViews="SellerAsAggregate, Demo, SellerAsDescriptionsListShowingReferenceView, SellerAsDescriptionsListShowingReferenceViewNoKey") 
	@ReferenceView(forViews="SellerAsAggregate2Levels", value="LevelNoDescriptionsList")
	@ReferenceView(forViews="SellerAsDescriptionsListShowingReferenceView", value="Simple")
	@ReferenceView(forViews="SellerAsDescriptionsListShowingReferenceViewNoKey", value="SimpleNoNumber")
	@ReferenceView(forViews="SellerInSection", value="CompleteWithCustomersWithSellerInSection") 
	@Action(forViews="SellerAsDescriptionsListShowingReferenceView", value="Customer.hideSeller")
	@DescriptionsList(
		forViews="SellerAsDescriptionsListShowingReferenceView, "
				+ "SellerAsDescriptionsListShowingReferenceViewNoKey, "
				+ "SellerAsDescriptionsListShowingReferenceViewNoFrameInSection", 
		showReferenceView=true) 
	private Seller seller; 
	
	@Transient @ManyToOne(fetch=FetchType.LAZY)
	@SearchListCondition(value="${name} = ${this.sellerNameInCondition}", forViews="SearchListCondition")
	private Seller transientSeller; 
	
	@DefaultValueCalculator(
		value=org.openxava.calculators.StringCalculator.class,
		properties={ @PropertyValue(name="string", value="GOOD") }
	)
	@Column(length=40, name="RELATIONSELLER")	
	private String relationWithSeller;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@OnChange(OnChangeVoidAction.class)	
	@ReferenceView("DecorateName") 
	@NoCreate(forViews="DEFAULT")
	@ReadOnly(forViews="SomeMembersReadOnly")
	@DescriptionsList(forViews="SomeMembersReadOnly", descriptionProperties="level.description, name")
	@SearchListCondition(value="${number} = ${this.seller.number}", forViews="SearchListCondition")
	private Seller alternateSeller;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private CustomerGroup group;
		
	@OneToMany(mappedBy="customer", cascade=CascadeType.ALL)
	@ListProperties("name, address, remarks, preferredWarehouse.name")	
	private Collection<DeliveryPlace> deliveryPlaces;
	
	@ManyToMany	
	@JoinTable(name="CUSTOMER_STATE",  
		joinColumns=@JoinColumn(name="CUSTOMER"), 
		inverseJoinColumns=@JoinColumn(name="STATE") 
	)
	private Collection<State> states;
	
	@Mask("L-A#0000")
	String passport;
	
	@Mask("0000 0000 0000 0000")
	String creditCard;
	
	@Column(length=40)	
	@Depends("address.zipCode, address.city")	
	public String getCity() {
		return getAddress().getZipCode() + " " + getAddress().getCity(); 
	}
	
	public boolean isLocal() {
		return false;
	}
		
 	public static Customer findByNumber(int number) throws NoResultException { 	 			
 		Query query = XPersistence.getManager().createQuery("from Customer as o where o.number = :number"); 
		query.setParameter("number", new Integer(number)); 
		return (Customer) query.getSingleResult();
 	} 	
	
 	public static Collection findAll()  {
		Query query = XPersistence.getManager().createQuery("from Customer as o"); 
		return query.getResultList();  		
 	} 
 	
 	public static Collection findByNameLike(String name)  { 		 			
 		Query query = XPersistence.getManager().createQuery("from Customer as o where o.name like :name order by o.name desc"); 
		query.setParameter("name", name); 
 		return query.getResultList();  		 		
 	} 	 	
 	
 	public static Collection findNormalOnes()  { 		 			
 		Query query = XPersistence.getManager().createQuery("from Customer as o where o.type = 0"); 
 		return query.getResultList();  		 		
 	} 	
 	public static Collection findSteadyOnes()  { 		 			
 		Query query = XPersistence.getManager().createQuery("from Customer as o where o.type = 1"); 
 		return query.getResultList();  		 		
 	} 	
 	public static Collection findByStreet(java.lang.String street)  { 			
 		Query query = XPersistence.getManager().createQuery("from Customer as o where o.address.street = :street"); 
		query.setParameter("street", street); 
 		return query.getResultList();  		
 	} 	
 	public static Collection findOrderedByState()  { 			
 		Query query = XPersistence.getManager().createQuery("from Customer as o order by o.address.state.name"); 
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

	public Seller getSeller() {
		return seller;
	}

	public void setSeller(Seller seller) {
		this.seller = seller;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getRelationWithSeller() {
		return relationWithSeller;
	}

	public void setRelationWithSeller(String relationWithSeller) {
		this.relationWithSeller = relationWithSeller;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

	public Address getAddress() {
		if (address != null) address.setCustomer(this);
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Seller getAlternateSeller() {
		return alternateSeller;
	}

	public void setAlternateSeller(Seller alternateSeller) {
		this.alternateSeller = alternateSeller;
	}

	public Collection<DeliveryPlace> getDeliveryPlaces() {
		return deliveryPlaces;
	}

	public void setDeliveryPlaces(Collection<DeliveryPlace> deliveryPlaces) {
		this.deliveryPlaces = deliveryPlaces;
	}



	public Collection<State> getStates() {
		return states;
	}



	public void setStates(Collection<State> states) {
		this.states = states;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getAdditionalEmails() {
		return additionalEmails;
	}

	public void setAdditionalEmails(String additionalEmails) {
		this.additionalEmails = additionalEmails;
	}

	public CustomerGroup getGroup() {
		return group;
	}

	public void setGroup(CustomerGroup group) {
		this.group = group;
	}

	public String getExtendedCity() {
		return extendedCity;
	}

	public void setExtendedCity(String extendedCity) {
		this.extendedCity = extendedCity;
	}

	public Seller getTransientSeller() {
		return transientSeller;
	}

	public void setTransientSeller(Seller transientSeller) {
		this.transientSeller = transientSeller;
	}
	
	public String getPassport() {
		return passport;
	}

	public void setPassport(String passport) {
		this.passport = passport;
	}
	
	public String getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}
	
	public String getSellerNameInCondition () {
		return "GALILEO GALILEI";
	}
}
