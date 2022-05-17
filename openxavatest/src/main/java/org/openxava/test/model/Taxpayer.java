package org.openxava.test.model;

import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
@View(members="name, address")
@View(name="Name", members="name")
public class Taxpayer extends TaxpayerBase {

}
