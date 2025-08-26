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
		if (name != null) {
			String[] parts = name.trim().split("\\s+");
			if (parts.length >= 2) {
				try {
					int n = Integer.parseInt(parts[1]);
					System.out.println("Journey.getSlowName() n=" + n);
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
		return name;
	}

	public String getUltraSlowName() { // Even slower than getSlowName() to stress on-demand fetch
		if (name != null) {
			String[] parts = name.trim().split("\\s+");
			if (parts.length >= 2) {
				try {
					int n = Integer.parseInt(parts[1]);
					System.out.println("Journey.getUltraSlowName() n=" + n);
					Thread.sleep(200); // So even the first 30 items take 6 seconds to load
				}
				catch (NumberFormatException ignore) {
					// Not a number, ignore
				}
				catch (InterruptedException e) {
				}
			}
		}
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
