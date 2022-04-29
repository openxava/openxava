package org.openxava.test.model;

import java.math.*;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@Tab(properties="mode, amount+, shipment.number, slow, shipment.description") 
public class ShipmentCharge {
	
	@Id @GeneratedValue(generator="system-uuid") @Hidden 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name="ID")
	private String oid;
	
	@Required
	@org.hibernate.annotations.Type(type="org.openxava.types.Base1EnumType", 
		parameters={			
			@Parameter(name="enumType", value="org.openxava.test.model.ShipmentCharge$Mode")
		}
	)	
	private Mode mode;
	public enum Mode { SLOW, MEDIUM, FAST };	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="SHIPMENT_TYPE", referencedColumnName="TYPE", insertable=false, updatable=false),
		@JoinColumn(name="MODE", referencedColumnName="MODE", insertable=false, updatable=false),
		@JoinColumn(name="SHIPMENT_NUMBER", referencedColumnName="NUMBER", insertable=false, updatable=false)
	})
	@DescriptionsList
	private Shipment shipment;
	@org.hibernate.annotations.Type(type="org.openxava.types.EnumLetterType", 
		parameters={
			@Parameter(name="letters", value="IE"), 
			@Parameter(name="enumType", value="org.openxava.test.model.Shipment$Type")
		}
	)				
	private Shipment.Type shipment_type;	
	private Integer shipment_number;	
	
	@Required @Stereotype("MONEY")
	@Column(name="TOTALAMOUNT") 
	private BigDecimal amount;
	
	public boolean isSlow(){
		return mode.compareTo(Mode.SLOW) == 0;
	}
	
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {		
		this.oid = oid;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public Shipment getShipment() {
		return shipment;
	}

	public void setShipment(Shipment shipment) {
		this.shipment = shipment;
		this.shipment_type = shipment == null?null:shipment.getType();
		this.shipment_number = shipment == null?null:shipment.getNumber();
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}
