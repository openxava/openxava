package org.openxava.test.model;

import org.openxava.model.*;
import javax.persistence.*;
import org.openxava.annotations.*;

@Entity
@View(members="value2; asEmbed3")
public class AsEmbed2 extends Identifiable {

	@Column(length=40)
	private String value2;
		
	@OneToOne(mappedBy = "asEmbed2")
	private AsEmbed1 asEmbed1;	
	
	@OneToOne
	@AsEmbedded
	private AsEmbed3 asEmbed3;

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}

	public AsEmbed1 getAsEmbed1() {
		return asEmbed1;
	}

	public void setAsEmbed1(AsEmbed1 asEmbed1) {
		this.asEmbed1 = asEmbed1;
	}

	public AsEmbed3 getAsEmbed3() {
		return asEmbed3;
	}

	public void setAsEmbed3(AsEmbed3 asEmbed3) {
		this.asEmbed3 = asEmbed3;
	}

}
