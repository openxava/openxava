package org.openxava.test.model;

import java.util.*;

import jakarta.persistence.*;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.CompositeType;
import org.hibernate.annotations.Parameter;
import org.openxava.annotations.*;
import org.openxava.calculators.*;

import lombok.*;

/**
 * Simplified version of {@link Delivery} mapped to the same table.
 * 
 * @author Javier Paniza
 */

@Entity
@Table(name="Delivery")
@IdClass(Delivery2Key.class)
@Tab(baseCondition="${delinv.year} = 2002") // Don't remove baseCondition, to test a case
@Getter @Setter
public class Delivery2 {

	@Id @Column(length=5)
	int number;

	@Id @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="invoice_year", referencedColumnName="year"),
		@JoinColumn(name="invoice_number", referencedColumnName="number")
	})
	@ReferenceView("Simple")
	Invoice delinv; // Not invoice, not deliveryInvoice, not "invoice" in the name, to test a case

	@Id @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="TYPE")
	@DescriptionsList
	DeliveryType type;

	@CompositeType(org.openxava.types.Date3Type.class)
	@AttributeOverrides({
		@AttributeOverride(name="year", column=@Column(name="year")),
		@AttributeOverride(name="month", column=@Column(name="month")),
		@AttributeOverride(name="day", column=@Column(name="day"))
	})
	@Required
	@DefaultValueCalculator(CurrentDateCalculator.class)
	Date date;

	String description;

}
