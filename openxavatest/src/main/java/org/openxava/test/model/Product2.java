package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.jpa.*;
import org.openxava.test.calculators.*;
import org.openxava.test.validators.*;

/**
 * Example of reference with descriptions-list.
 * This is the preferred way to do it, 
 * but also it is possible to be done using stereotypes, 
 * as it shows Product
 * 
 * @author Javier Paniza
 */

@Entity
@Table(name="PRODUCT")
@EntityValidators({
	@EntityValidator(value=org.openxava.test.validators.CheapProductValidator.class, properties= {
		@PropertyValue(name="limit", value="100"),
		@PropertyValue(name="description"),
		@PropertyValue(name="unitPrice")
	}),
	@EntityValidator(value=org.openxava.test.validators.ExpensiveProductValidator.class, properties= {
		@PropertyValue(name="limit", value="1000"),
		@PropertyValue(name="description"),
		@PropertyValue(name="unitPrice")
	})
})
@Views({
	@View( members=
		"#number, description;" + // Don't remove #, it's needed to test some bugs	
		"color;" + 
		"family, subfamily;" +
		"warehouse, zoneOne;" +
		"unitPrice, unitPriceInPesetas;" +
		"unitPriceWithTax;" +
		"photos"
	),
	@View( name="ColorWithFrame", members= 
		"number;" +
		"description;" +
		"photos;" +
		"color;" + 
		"family;" +
		"subfamily;" +
		"warehouse, zoneOne;" +
		"unitPrice, unitPriceInPesetas;"		
	),
	@View(name="ReferenceAndStereotype", members="number; family; subfamilyNumber;"),
	@View(name="WithFormula", members=
		"number;" +
		"description;" +
		"family;" +
		"subfamily;" +
		"formula;" +
		"unitPrice, unitPriceInPesetas;"		
	),
	@View(name="WithFormulaAsAggregate", members=
		"number;" +
		"description;" +
		"family;" +
		"subfamily;" +
		"formula;" +
		"unitPrice, unitPriceInPesetas;" 
	),
	@View(name="SimpleWithFamily", members="number, description, unitPrice; family"),
	@View(name="OnlySoftware"),
	@View( name = "Simple15", members=
		"number, description;" + 	
		"color;" +  
		"family, subfamily;" +
		"warehouse, unitPrice, unitPriceWithTax, unitPriceWithTax;" +
		"zoneOne;" +
		"zone11, zone12, zone13, zone14, zone15"
	),
	@View( name = "NotSimple16", members=
		"number, description;" + 	
		"color;" +  
		"family, subfamily;" +
		"warehouse, unitPrice, unitPriceWithTax, unitPriceWithTax;" +
		"zoneOne;" +
		"zone11, zone12, zone13, zone14, zone15, zone16"
	),	
	@View( name = "NotSimpleReference", members=
		"number, description;" + 	
		"color, unitPrice;" + 
		"family, subfamily;" 
	),	
	@View( name = "NotSimpleGroup", members=
		"number, description;" + 	
		"color, unitPrice;" + 
		"families [ family, subfamily; ]" 
	),	
	@View( name = "NotSimpleSection", members=
		"number, description;" + 	
		"colores { color, unitPrice; }" + 
		"families { family, subfamily; }" 
	),	
	@View( name = "NotSimpleFramedEditor", members=
		"number, color, unitPrice;" + 
		"description" 
	),
	@View( name = "DescriptionsListShowsReferenceView", members=
		"number, description;" + 	
		"family; " +
		"subfamily;" 
	),
	@View( name = "SectionByDescriptionsList", members=
		"number, description;" + 	
		"family { family }" +  
		"subfamily { subfamily }" 
	)	
})
@Tab(properties="number, description, family.description, subfamily.description, unitPrice, unitPriceWithTax, subfamily.family.description") 
@Tab(name="Editable", 
	properties="number, description, family.description, unitPrice, warehouse.name, extendedDescription",
	editableProperties="family.description, unitPrice, warehouse.name, extendedDescription"
)
public class Product2 {
	
	@Id @Column(length=10) 
	private long number;
	
	@Column(length=40) @Required
	@PropertyValidators ({
		@PropertyValidator(value=ExcludeStringValidator.class, properties=
			@PropertyValue(name="string", value="MOTO")
		),
		@PropertyValidator(value=ExcludeStringValidator.class, properties=
			@PropertyValue(name="string", value="COCHE")
		)		
	})
	@Editor(forViews="NotSimpleFramedEditor", value="TextArea")
	private String description;
	
	@Stereotype("IMAGES_GALLERY")
	private String photos;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@Editor(forViews="ColorWithFrame", value="ColorFrame") 
	private Color color; 

	@ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="FAMILY")
	@DefaultValueCalculator(value=IntegerCalculator.class, properties=
		@PropertyValue(name="value", value="2")
	)	
	@DescriptionsList(orderByKey=true, notForViews="SimpleWithFamily, NotSimpleReference")   
	private Family2 family;	
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="SUBFAMILY") @NoCreate
	@DescriptionsLists({
		@DescriptionsList(
			descriptionProperties="description", // In this case descriptionProperties can be omitted
			depends="family",
			condition="${family.number} = ?"
		),
		@DescriptionsList( forViews="OnlySoftware", 		
			condition="${family.description} like 'SOFTWARE%'" // Must contain a % to test a case
		),
		@DescriptionsList( forViews="DescriptionsListShowsReferenceView",
			showReferenceView=true, 
			depends="family",
			condition="${family.number} = ?"
		),
		@DescriptionsList( forViews="SectionByDescriptionsList",
			depends="this.family", // With "this." to test a case
			condition="${family.number} = ?"
		),		
	})
	private Subfamily2 subfamily;

	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumns({ 
		@JoinColumn(name="ZONE", referencedColumnName="ZONE"), 
		@JoinColumn(name="WAREHOUSE", referencedColumnName="NUMBER") 
	})
	@DefaultValueCalculator(DefaultWarehouseCalculator.class)
	@DescriptionsList
	@OnChange(org.openxava.test.actions.OnChangeWarehouseAction.class)
	private Warehouse warehouse;

	@Money 
	@Required
	@DefaultValueCalculator(value=DefaultProductPriceCalculator.class, properties=
		@PropertyValue(name="familyNumber")
	)
	@PropertyValidator(UnitPriceValidator.class)
	private BigDecimal unitPrice;
	
	@org.hibernate.annotations.Formula("UNITPRICE * 1.16")
	private BigDecimal unitPriceWithTax;
	public BigDecimal getUnitPriceWithTax() {
		return unitPriceWithTax;
	}	
	
	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="FORMULA_OID")
	@ReferenceView("Simple") 
	@AsEmbedded(forViews="WithFormulaAsAggregate")
	private Formula formula;
	
	// Only to show in view
	@Transient @Stereotype("SUBFAMILY_DEPENDS_REFERENCE")
	private int subfamilyNumber; 
	
	@org.hibernate.annotations.Formula("NUMBER || ' - ' || DESCRIPTION")
	private String extendedDescription; 
	
	@Depends("unitPrice") 
	@Max(999999999999999999L) 	
	public BigDecimal getUnitPriceInPesetas() {
		if (unitPrice == null) return null;
		return unitPrice.multiply(new BigDecimal("166.386")).setScale(0, BigDecimal.ROUND_HALF_UP);
	}
	
	@Label 
	public String getZoneOne() {
		return "IN ZONE 1";
	}
	
	@Label 
	public String getZone11() {
		return "IN ZONE 11";
	}	
	
	@Label 
	public String getZone12() {
		return "IN ZONE 12";
	}
	
	@Label 
	public String getZone13() {
		return "IN ZONE 13";
	}
	
	@Label 
	public String getZone14() {
		return "IN ZONE 14";
	}
	
	@Label 
	public String getZone15() {
		return "IN ZONE 15";
	}
	
	@Label 
	public String getZone16() {
		return "IN ZONE 16";
	}	
	
 	public static Product2 findByNumber(long number) throws NoResultException { 	 			
 		Query query = XPersistence.getManager().createQuery("from Product2 as o where o.number = :number"); 
		query.setParameter("number", new Long(number));  	
		return (Product2) query.getSingleResult();
	} 

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Family2 getFamily() {
		return family;
	}

	public void setFamily(Family2 family) {
		this.family = family;
	}

	public Subfamily2 getSubfamily() {
		return subfamily;
	}

	public void setSubfamily(Subfamily2 subfamily) {
		this.subfamily = subfamily;
	}

	public Warehouse getWarehouse() {
		// In this way because the columns for warehouse can contain
		// 0 for no value
		try {
			if (warehouse != null) warehouse.toString(); // to force load
			return warehouse;
		}
		catch (EntityNotFoundException ex) {			
			return null;  
		}
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public int getSubfamilyNumber() {
		return subfamilyNumber;
	}

	public void setSubfamilyNumber(int subfamilyNumber) {
		this.subfamilyNumber = subfamilyNumber;
	}

	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getExtendedDescription() {
		return extendedDescription;
	}

	public void setExtendedDescription(String extendedDescription) {
		this.extendedDescription = extendedDescription;
	}
	

}
