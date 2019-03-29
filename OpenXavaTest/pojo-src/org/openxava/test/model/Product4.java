package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.test.calculators.*;
import org.openxava.test.validators.*;
import org.openxava.util.*;

/**
 * As Product2 but uses property-based access. <p>
 * 
 * In this class the getters are annotated instead of fields.<br>
 * 
 * @author Javier Paniza
 */

@Entity
@Table(name="PRODUCT")
@Views({
	@View( members=
		"#number, description, photos;" + 
		"family, subfamily;" +
		"warehouse, zoneOne;" +
		"unitPrice, unitPriceInPesetas;"		
	),
	@View( name= "NoDescriptionsLists", members=
		"number, description, photos;" +
		"datos [#" + // Don't change this group to test a layout case
			"family, subfamily;" +
			"warehouse, unitPrice;" +
		"];"	+
		"zoneOne, unitPriceInPesetas;"		
	),	
	@View( name="NotAligned", members=
		"number, description, photos;" + 
		"family, subfamily;" +
		"warehouse, zoneOne;" +
		"unitPrice, unitPriceInPesetas;"		
	)	
})
@Tab(properties="number, description, family.description, subfamily.description")
public class Product4 {
		 
	private long number;	
	private String description;	
	private String photos;
	private Family2 family;		
	private Subfamily2 subfamily;
	private Warehouse warehouse;
	private BigDecimal unitPrice;	
	private Formula formula;	
	private int subfamilyNumber; 
	
	@PrePersist
	public void validate() throws org.openxava.validators.ValidationException {
		if (getDescription().contains("OPENXAVA")) {
			throw new org.openxava.validators.ValidationException("openxava_not_saleable"); 
		}
		if (getDescription().contains("ECLIPSE")) {
			throw new javax.validation.ValidationException("eclipse_not_saleable"); 
		}
		if (getNumber() == 666) {
			throw new javax.validation.ValidationException(
				XavaResources.getString("invalid_state", 
					Labels.get("number"), Labels.get(getClass().getSimpleName()), 
					XavaResources.getString("number_of_man"), getNumber()));
		}
	}
	
	@PreRemove
	public void validateOnRemove() { 		
		if (number == 1) {
			throw new javax.validation.ValidationException("one_not_deletable");			
		}		
		if (number == 2) {
			throw new javax.validation.ValidationException("two_not_deletable");
		}				

		if (family != null) {
			throw new org.openxava.validators.ValidationException("has_family"); 
		}
	}
		
	@Id @Column(length=10)
	public long getNumber() {
		return number;
	}
	
	public void setNumber(long number) {
		this.number = number;
	}
	
	@Column(length=40) @Required
	@PropertyValidators ({
		@PropertyValidator(value=ExcludeStringValidator.class, properties=
			@PropertyValue(name="string", value="MOTO")
		),
		@PropertyValidator(value=ExcludeStringValidator.class, properties=
			@PropertyValue(name="string", value="COCHE")
		)		
	})			
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Stereotype("IMAGES_GALLERY")	
	public String getPhotos() {
		return photos;
	}

	public void setPhotos(String photos) {
		this.photos = photos;
	}

	@Stereotype("MONEY") @Required
	@DefaultValueCalculator(value=DefaultProductPriceCalculator.class, properties=
		@PropertyValue(name="familyNumber")
	)
	@PropertyValidator(UnitPriceValidator.class)	
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	@ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="FAMILY")
	@DefaultValueCalculator(value=IntegerCalculator.class, properties=
		@PropertyValue(name="value", value="2")
	)
	@DescriptionsList(notForViews="NoDescriptionsLists", orderByKey=true)
	@ReferenceView(forViews="NoDescriptionsLists", value="Number") 
	@NoFrame(forViews="NoDescriptionsLists") 
	public Family2 getFamily() {
		return family;
	}

	public void setFamily(Family2 family) {
		this.family = family;
	}

	@ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="SUBFAMILY") @NoCreate
	@DescriptionsList(notForViews="NoDescriptionsLists",
		descriptionProperties="description", // In this case descriptionProperties can be omitted
		depends="family",
		condition="${family.number} = ?"
	)
	@ReferenceView(forViews="NoDescriptionsLists", value="Number") 
	@NoFrame(forViews="NoDescriptionsLists") 
	public Subfamily2 getSubfamily() {
		return subfamily;
	}

	public void setSubfamily(Subfamily2 subfamily) {
		this.subfamily = subfamily;
	}

	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumns({ 
		@JoinColumn(name="ZONE", referencedColumnName="ZONE"), 
		@JoinColumn(name="WAREHOUSE", referencedColumnName="NUMBER") 
	})
	@DefaultValueCalculator(DefaultWarehouseCalculator.class)
	@DescriptionsList(notForViews="NoDescriptionsLists")
	@ReferenceView(forViews="NoDescriptionsLists", value="Number") 
	@NoFrame(forViews="NoDescriptionsLists") 
	@OnChange(org.openxava.test.actions.OnChangeWarehouseAction.class)	
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

	// Only to show in view
	@Transient @Stereotype("SUBFAMILY_DEPENDS_REFERENCE")	
	public int getSubfamilyNumber() {
		return subfamilyNumber;
	}

	public void setSubfamilyNumber(int subfamilyNumber) {
		this.subfamilyNumber = subfamilyNumber;
	}

	@ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="FORMULA_OID")
	@ReferenceView("Simple") 
	@AsEmbedded(forViews="WithFormulaAsAggregate")	
	public Formula getFormula() {
		return formula;
	}

	public void setFormula(Formula formula) {
		this.formula = formula;
	}
	
	@Transient @Depends("unitPrice")  
	@Digits(integer=18, fraction=0) 
	public BigDecimal getUnitPriceInPesetas() {
		if (unitPrice == null) return null;
		return unitPrice.multiply(new BigDecimal("166.386")).setScale(0, BigDecimal.ROUND_HALF_UP);
	}
	
	@Transient @Stereotype("LABEL") 
	public String getZoneOne() {
		return "In ZONE 1";
	}		

}
