package org.openxava.test.model;
 
import java.math.*;
import java.time.*;
import java.util.*;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.openxava.annotations.*;
import org.openxava.calculators.*;
import org.openxava.jpa.*;
import org.openxava.model.*;

import lombok.*;
 
/**
 * 
 * @author Javier Paniza
 */

@Entity @Getter @Setter
@View(members=
	"year, number, date," + // The members for the header part in one line
	"data {" + // A tab 'data' for the main data of the document
	    "customer;" +
	    "details;" +
	    "remarks" +
	"}" 
)
abstract public class CommercialDocument extends Identifiable {

    @Column(length=4)
    @DefaultValueCalculator(CurrentYearCalculator.class) // Current year
    @SearchKey
    int year;
 
    @Column(length=6)
    @ReadOnly // The user cannot modify the value
    @SearchKey
    int number;
 
    @Required
    @DefaultValueCalculator(CurrentLocalDateCalculator.class) // Current date
    LocalDate date;
 
    @ManyToOne(fetch=FetchType.LAZY, optional=false) // Customer is required
    @ReferenceView("Simple") // The view named 'Simple' is used to display this reference
    Customer customer;
    
    @ElementCollection
    @ListProperties(
            "product.number, product.description, quantity, pricePerUnit, " +
            "amount+[" + 
            	"commercialDocument.vatPercentage," +
            	"commercialDocument.vat," +
            	"commercialDocument.totalAmount" + // With a @Calculation property to test a case
            "]" 
        )
    Collection<CommercialDocumentDetail> details;
    
    @TextArea
    String remarks;
 
    @Digits(integer=2, fraction=0) // To indicate its size
    @DefaultValueCalculator(value = org.openxava.calculators.BigDecimalCalculator.class, 
    	properties = {
    		@PropertyValue(name = "value", value = "21") 
    	}
    )
    BigDecimal vatPercentage;
       
    @ReadOnly
    @Money
    @Calculation("sum(details.amount) * vatPercentage / 100")
    BigDecimal vat;
    
    @org.hibernate.annotations.Formula("TOTALAMOUNT * 0.10") // The calculation using SQL
    @Setter(AccessLevel.NONE) // The setter is not generated, only the getter is needed
    @Money
    BigDecimal estimatedProfit; // A field, as in the persistent property case

    @ReadOnly
    @Money
    @Calculation("sum(details.amount) + vat") // Keep as @Calculation to test a case with resizing   
    BigDecimal totalAmount;    
    
    @PrePersist // Executed just before saving the object for the first time
    private void calculateNumber() throws Exception {
        Query query = XPersistence.getManager()
            .createQuery("select max(i.number) from " +
            getClass().getSimpleName() + // This it's valid for both Invoice and Order
            " i where i.year = :year");
        query.setParameter("year", year);
        Integer lastNumber = (Integer) query.getSingleResult();
        this.number = lastNumber == null ? 1 : lastNumber + 1;
    }
    
    public String toString() {
        return year + "/" + number;
    }
    
    
}
