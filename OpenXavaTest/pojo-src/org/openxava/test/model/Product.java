package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.test.actions.*;
import org.openxava.test.calculators.*;
import org.openxava.test.validators.*;

/**
 * This example uses stereotypes for family and subfamily.
 * It is better to use reference (like in Product2), but
 * therefore it's show some features more advances of stereotypes.
 * 
 * @author Javier Paniza
 */

@Entity
@EntityValidator(value=org.openxava.test.validators.CheapProductValidator.class, properties= {
	@PropertyValue(name="limit", value="100"),
	@PropertyValue(name="description"),
	@PropertyValue(name="unitPrice")
})
@EntityValidator(value=org.openxava.test.validators.ExpensiveProductValidator.class, properties= {
	@PropertyValue(name="limit", value="1000"),
	@PropertyValue(name="description"),
	@PropertyValue(name="unitPrice")
})
@EntityValidator(value=org.openxava.test.validators.ForbiddenPriceValidator.class, 
	properties= {
		@PropertyValue(name="forbiddenPrice", value="555"),
		@PropertyValue(name="unitPrice")
	},
	onlyOnCreate=true
)	

@Views({
	@View( name = "WithSection" , members = 
		"number;" +
		"data {" +		
		"	description;" +
		"	photos;" +
		"	familyNumber;" +
		"	subfamilyNumber;" +
		"	warehouseKey;" +
		"	price [" +
		"		unitPrice;" +
		"		unitPriceInPesetas;" +
		"	]" +
		"	remarks" +		
		"}"		
	),
	@View( name="Simple", members = "number, description, unitPrice" ),
	@View( name="EditPrice", members = "number, description, unitPrice"),
	@View( name="RichDescriptionPhotos", members = "number; description; photos;" ) 
})

@Tab(properties = "number, description, unitPrice, unitPriceInPesetas") 
public class Product { 
	
	@Id @Column(length=10) 
	private long number;
	
	@Column(length=40) @Required
	@PropertyValidator(value=ExcludeStringValidator.class, properties=
		@PropertyValue(name="string", value="MOTO")
	)
	@PropertyValidator(value=ExcludeStringValidator.class, properties=
		@PropertyValue(name="string", value="COCHE")
	)		
	@PropertyValidator(value=ExcludeStringValidator.class, properties=			
		@PropertyValue(name="string", value="CUATRE"),
		onlyOnCreate=true
	)
	@ReadOnly(forViews="EditPrice")
	@Editor(forViews="RichDescriptionPhotos", value="HtmlText") 
	private String description;

	@Stereotype("IMAGES_GALLERY")
	private String photos;
	
	@Stereotype("FAMILY") @Required @Column(name="FAMILY")
	private int familyNumber;
	
	@Stereotype("SUBFAMILY") @Required @Column(name="SUBFAMILY")
	private int subfamilyNumber;
	
	@Column(name="ZONE")
	private Integer warehouseZoneNumber;
	
	@Column(name="WAREHOUSE")
	private Integer warehouseNumber;
		
	@Stereotype("MONEY") @Required
	@Editor(forViews="Simple", value="Money") // Though it has MONEY stereotype it does not
											// use Money editor because Product.unitPrice
											// is overwritten in editors.xml. Using @Editor
											// we overwrite the overwriting only for Simple view
	@DefaultValueCalculator(value=DefaultProductPriceCalculator.class, properties=
		@PropertyValue(name="familyNumber")
	)	
	@PropertyValidator(UnitPriceValidator.class)
	@OnChange(forViews="DEFAULT, WithSection",
		value=OnChangeProductUnitPriceAction.class
	)	
	private BigDecimal unitPrice;
		
	@Stereotype("MEMO")
	private String remarks;

	@Depends("unitPrice")  
	@Digits(integer=10, fraction=0) 
	public BigDecimal getUnitPriceInPesetas() {
		if (unitPrice == null) return null;
		return unitPrice.multiply(new BigDecimal("166.386")).setScale(0, BigDecimal.ROUND_HALF_UP);
	}
	
	public void increasePrice() {
		setUnitPrice(getUnitPrice().multiply(new BigDecimal("1.02")).setScale(2));
	}
	
	public BigDecimal getPrice(String country, BigDecimal tariff) throws ProductException, PriceException {	
		if ("España".equals(country) || "Guatemala".equals(country)) {
			return getUnitPrice().add(tariff);   
		}
		else {
			throw new PriceException("Country not register");
		}	
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFamilyNumber() {
		return familyNumber;
	}

	public void setFamilyNumber(int familyNumber) {
		this.familyNumber = familyNumber;
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getSubfamilyNumber() {
		return subfamilyNumber;
	}

	public void setSubfamilyNumber(int subfamilyNumber) {
		this.subfamilyNumber = subfamilyNumber;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	@Stereotype("WAREHOUSE") @Required
	@OnChange(forViews="WithSection",
		value=org.openxava.test.actions.OnChangeVoidAction.class
	)
	public Warehouse getWarehouseKey() {
		if (this.warehouseNumber == null) return null;
		Warehouse key = new Warehouse();
		key.setNumber(this.warehouseNumber);
		key.setZoneNumber(this.warehouseZoneNumber);
		return key;
	}

	public void setWarehouseKey(Warehouse warehouseKey) {	
		if (warehouseKey == null) {
			this.warehouseZoneNumber = null;
			this.warehouseNumber = null;						
		}
		else {
			this.warehouseZoneNumber = warehouseKey.getZoneNumber();
			this.warehouseNumber = warehouseKey.getNumber();			
		}
	}
	
}
