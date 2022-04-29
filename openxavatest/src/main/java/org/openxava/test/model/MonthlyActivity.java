package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.test.validators.*;


/**
 * 
 * @author Javier Paniza
 */

@Entity
@EntityValidator( value=MonthNameValidator.class,
properties={
	@PropertyValue(name="monthName", from = "name")
})
public class MonthlyActivity extends Nameable {

}
