package org.openxava.annotations;

import java.lang.annotation.*;

/**
 * A group of <code>@{@link Tab}</code> associated to the same entity. <p>
 * 
 * Applies to entities.<p>
 * 
 * Example:
 * <pre>
 * &nbsp;@Entity
 * &nbsp;@Tabs({
 * &nbsp;&nbsp;&nbsp;@Tab(properties="year, number, date, amountsSum, vat, detailsCount, paid, importance"),
 * &nbsp;&nbsp;&nbsp;@Tab(name="Level4Reference", properties="year, number, customer.seller.level.description"),
 * &nbsp;&nbsp;&nbsp;@Tab(name="Simple", properties="year, number, date", 
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;defaultOrder="${year} desc, ${number} desc"
 * &nbsp;&nbsp;&nbsp;)
 * &nbsp;}) 
 * &nbsp;public class Invoice {
 * &nbsp;...
 * </pre>
 * 
 * Since 6.1 @Tab is repeatable, so you don't need to use @Tabs any more.
 * 
 * @author Javier Paniza
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Tabs {
	
	Tab [] value();
	
}
