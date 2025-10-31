package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.calculators.*;

import lombok.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity @Getter @Setter
public class Ledger {

	@Id @Hidden
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(length=10)
	private Integer oid;

    @ManyToOne( fetch=FetchType.LAZY) 
    @DescriptionsList
    @DefaultValueCalculator(CurrentLedgerPeriodCalculator.class) 
    LedgerPeriod period;

    @Column(length=6)
    @DefaultValueCalculator(value=NextNumberForLedgerPeriodCalculator.class,
   		properties=@PropertyValue(name="periodId", from="period.id")
    )
    int number;

    @Column(length=80)
    String description;
    
}