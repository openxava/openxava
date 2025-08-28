package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Tab(properties="name, averageSpeed.speed, description")
@View(members="name; averageSpeed; description")
@Entity
public class Journey {
	
	@Id @Hidden
	private String oid;	
	
	@Column(length=20)
	private String name;
	
	@ManyToOne(fetch=FetchType.LAZY)
	private AverageSpeed averageSpeed;
		
	@Column(length=40)
	private String description;

	public String getSlowName() { // To test on demand fetch in server side
		/* tmr 
		if (name != null) {
			String[] parts = name.trim().split("\\s+");
			if (parts.length >= 2) {
				try {
					int n = Integer.parseInt(parts[1]);
					if (n > 180) {
						Thread.sleep(100);
					}
				}
				catch (NumberFormatException ignore) {
					// Not a number, ignore
				}
				catch (InterruptedException e) {
				}
			}
		}
		*/
		return name;
	}

	// Always slow even with the first elements, to test a case. 
	// Currently +12 seconds, but never less than 2 seconds (DescriptionsListTest.testLargeDatasetLoadedOnDemand()).
	// You can test it is slow, opening the combo in SlowTraveler.
	public String getUltraSlowName() { 
		/* tmr 
		try {
			Thread.sleep(200); // So even the first 30 items take 6 seconds to load
		}
		catch (InterruptedException e) {
		}
		*/
		return name;
	}

	
	@PrePersist
	private void generateOid() { 
		oid = "z" + System.currentTimeMillis();
	}
	
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public AverageSpeed getAverageSpeed() {
		return averageSpeed;
	}

	public void setAverageSpeed(AverageSpeed averageSpeed) {
		this.averageSpeed = averageSpeed;
	}
		
}
