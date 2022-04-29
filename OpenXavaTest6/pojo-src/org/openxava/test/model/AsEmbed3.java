package org.openxava.test.model;

import org.openxava.model.*;
import javax.persistence.*;
import org.openxava.annotations.*;

@Entity
@View(members="value3")
public class AsEmbed3 extends Identifiable {

	@Column(length=40)
	private String value3;
		
	@OneToOne(mappedBy = "asEmbed3")
	private AsEmbed2 asEmbed2;

	public String getValue3() {
		return value3;
	}

	public void setValue3(String value3) {
		this.value3 = value3;
	}

	public AsEmbed2 getAsEmbed2() {
		return asEmbed2;
	}

	public void setAsEmbed2(AsEmbed2 asEmbed2) {
		this.asEmbed2 = asEmbed2;
	}	

}
