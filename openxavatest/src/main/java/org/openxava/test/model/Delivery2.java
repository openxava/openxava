package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
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

	@Type(type="org.openxava.types.Date3Type")
	@Columns(columns = { @Column(name="year"), @Column(name="month"), @Column(name="day") })
	@Required
	@DefaultValueCalculator(CurrentDateCalculator.class)
	Date date;

	String description;

}
