package org.openxava.test.model;

import java.util.*;
import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * To test the AsserTrue message of MotorVehicle
 * on a collection of embeddables
 *
 * @author Jeromy Altuna
 */

@Entity
@View(members="name; approvedDrivingTest; vehicles")
public class MotorVehicleDriver2 extends MotorVehicleDriver {
	
	@OneToMany(mappedBy="driver", cascade=CascadeType.REMOVE)
	public Collection<MotorVehicle> getVehicles() {
		return super.getVehicles();
	}
	
}
