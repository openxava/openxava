package org.openxava.test.model;

import java.math.*;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.jpa.*;
import org.openxava.test.calculators.*;
import org.openxava.test.validators.*;

/** 
 * Example of combining an enum and a reference with descriptions-list.
 * 
 * @author Javier Paniza
 */

@Entity
@Table(name="PRODUCT")
@Views({
	@View( members=
		"number;" +
		"description;" +
		"photos;" +
		"color;" + 
		"family;" +
		"subfamily;" +
		"warehouse, zoneOne;" +
		"unitPrice, unitPriceInPesetas;" +
		"unitPriceWithTax;" +
		"productDetailsSupplierContactDetails"
	),
	@View( name="FamilyFromSubfamily", members= 
		"number;" +
		"data [" + 
		"	description, " +
		"	subfamily;" +
		"]" 		
	),
	@View( name="Dialog", members="description;")
})
public class Product5 {
	
	@Id @Column(length=10) 
	private long number;
	
	@Column(length=40) @Required
	private String description;
	
	@Editor("GalleryNoDialog") 
	private String photos;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private Color color; 

	public enum Family { NONE, SOFTWARE, HARDWARE, SERVICIOS }

	@Enumerated(EnumType.ORDINAL)
	private Family family;	
	
	@ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="SUBFAMILY") 
	@DescriptionsList(notForViews="FamilyFromSubfamily", 
		depends="family",
		condition="${family.number} = ?"
	)
	@NoFrame(forViews="FamilyFromSubfamily") 
	@ReferenceView(forViews="FamilyFromSubfamily", value="WithFamilyNoFrame") 
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

	@Stereotype("MONEY") @Required
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
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	private Collection<ProductDetailsSupplierContactDetails> productDetailsSupplierContactDetails;
	
	@Depends("unitPrice") 
	@Max(999999999999999999L) 	
	public BigDecimal getUnitPriceInPesetas() {
		if (unitPrice == null) return null;
		return unitPrice.multiply(new BigDecimal("166.386")).setScale(0, BigDecimal.ROUND_HALF_UP);
	}
	
	@Stereotype("LABEL")
	public String getZoneOne() {
		return "IN ZONE 1";
	}
	
 	public static Product5 findByNumber(long number) throws NoResultException { 	 			
 		Query query = XPersistence.getManager().createQuery("from Product2 as o where o.number = :number"); 
		query.setParameter("number", new Long(number));  	
		return (Product5) query.getSingleResult();
	} 

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description.contains("OPENXAVA")) {
			throw new org.openxava.validators.ValidationException("openxava_not_saleable"); 
		}
		if (description.contains("ECLIPSE")) {
			throw new javax.validation.ValidationException("eclipse_not_saleable"); 
		}
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

	public Family getFamily() {
		return family;
	}

	public void setFamily(Family family) {
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

	public void setProductDetailsSupplierContactDetails(
			Collection<ProductDetailsSupplierContactDetails> productDetailsSupplierContactDetails) {
		this.productDetailsSupplierContactDetails = productDetailsSupplierContactDetails;
	}

	public Collection<ProductDetailsSupplierContactDetails> getProductDetailsSupplierContactDetails() {
		return productDetailsSupplierContactDetails;
	}
	

}
