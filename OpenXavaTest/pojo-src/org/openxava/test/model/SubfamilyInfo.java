package org.openxava.test.model;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza
 */

@Embeddable
public class SubfamilyInfo {
	
	
	@ManyToOne(fetch=FetchType.LAZY) @DescriptionsList
	private Family2 family;
	
	@ManyToOne(fetch=FetchType.LAZY)  
	@DescriptionsList(depends="this.family", condition="${family.number} = ?")
	private Subfamily2 subfamily;

	public Family2 getFamily() {
		// In this way for supporting '0' as value in database for no family
		try {
			if (family != null) family.toString(); // to force load
			return family;
		}
		catch (EntityNotFoundException ex) {			
			return null;  
		}		
	}

	public void setFamily(Family2 family) {
		this.family = family;
	}

	public Subfamily2 getSubfamily() {
		// In this way for supporting '0' as value in database for no subfamily
		try {
			if (subfamily != null) subfamily.toString(); // to force load
			return subfamily;
		}
		catch (EntityNotFoundException ex) {			
			return null;  
		}		

	}

	public void setSubfamily(Subfamily2 subfamily) {
		this.subfamily = subfamily;
	}
	
}
