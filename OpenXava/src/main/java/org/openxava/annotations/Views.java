package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link View}</code> associated to the same entity. <p>
 * 
 * Applies to entities.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@Entity
 * &nbsp;@Views({
 * &nbsp;&nbsp;&nbsp;@View(members=
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"year, number, date, paid;" +
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"discounts [" +
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"	customerDiscount, customerTypeDiscount, yearDiscount;" +
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"];" +
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"comment;" +			
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"customer { customer }" +
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"details { details }" +			
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"amounts { amountsSum; vatPercentage; vat }" +
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"deliveries { deliveries }"		
 * &nbsp;&nbsp;&nbsp;),
 * &nbsp;&nbsp;&nbsp;@View(name="Simple", members="year, number, date, yearDiscount;"),
 * &nbsp;&nbsp;&nbsp;@View(name="NestedSections", members= 	
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"year, number, date;" + 		  		
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"customer { customer }" +
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"data {" +				 
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"	details { details }" +
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"	amounts {" +
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"		vat { vatPercentage; vat }" +				
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"		amountsSum { amountsSum }" +
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"	}" +				
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"}" +						
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"deliveries { deliveries }"		
 * &nbsp;&nbsp;&nbsp;),
 * &nbsp;&nbsp;&nbsp;@View(name="Deliveries", members=
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"year, number, date;" +
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"deliveries;"
 * &nbsp;&nbsp;&nbsp;),
 * &nbsp;&nbsp;&nbsp;@View(name="Amounts", members=
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"year, number;" +
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"amounts [#" + 
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"	customerDiscount, customerTypeDiscount, yearDiscount;" +
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"	amountsSum, vatPercentage, vat;" +
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"]"			
 * &nbsp;&nbsp;&nbsp;)
 * &nbsp;}) 
 * &nbsp;public class Invoice {
 * &nbsp;...
 * </pre>
 * 
 * Since 6.1 @View is repeatable, so you don't need to use @Views any more.
 *
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Views {
	
	View [] value();
	
}
