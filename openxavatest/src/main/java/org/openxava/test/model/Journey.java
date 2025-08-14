package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

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

	// Static counter for slowName calls; resets if idle > 2s
	private static int slowNameCount = 0; // tmr
	private static long slowNameLastTsMs = 0L; // tmr

	public String getSlowName() { // tmr Debería hacer un @Tab que no lo incluya, para no afectar lo demás
		long now = System.currentTimeMillis();
		if (now - slowNameLastTsMs > 2000) { // reset if idle > 2s
			slowNameCount = 0;
		}
		slowNameLastTsMs = now;
		int current = ++slowNameCount;
		System.out.println("getSlowName() called - count=" + current); // tmr Quitar
		// If the second token in name is a number > 100, throw a runtime exception
		/* tmr 
		if (name != null) {
			String[] parts = name.trim().split("\\s+");
			if (parts.length >= 2) {
				try {
					int n = Integer.parseInt(parts[1]);
					if (n > 100) {
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
