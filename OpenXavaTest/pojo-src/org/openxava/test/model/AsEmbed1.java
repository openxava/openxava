package org.openxava.test.model;

import org.openxava.model.*;
import javax.persistence.*;
import org.openxava.annotations.*;

@Entity
@View(members="value1; asEmbed2")
@Tab(properties="value1, asEmbed2.value2, container.value") 
public class AsEmbed1 extends Identifiable {

	@ManyToOne(fetch = FetchType.LAZY)
	private AsEmbedContainer container;
		
	@Column(length=40)
	private String value1;
	
	@OneToOne
	@AsEmbedded
	private AsEmbed2 asEmbed2;

	public AsEmbedContainer getContainer() {
		return container;
	}

	public void setContainer(AsEmbedContainer container) {
		this.container = container;
	}

	public String getValue1() {
		return value1;
	}

	public void setValue1(String value1) {
		this.value1 = value1;
	}

	public AsEmbed2 getAsEmbed2() {
		return asEmbed2;
	}

	public void setAsEmbed2(AsEmbed2 asEmbed2) {
		this.asEmbed2 = asEmbed2;
	}
	
}
