package org.openxava.test.model;

import javax.persistence.*;

@Entity 
public class DealAssured { 

	@Id
	private Long id;
	
	@Column(length=40)
	private String name;
	
	@OneToOne(mappedBy="assured", fetch=FetchType.LAZY)
	private Deal deal;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Deal getDeal() {
		return deal;
	}

	public void setDeal(Deal deal) {
		this.deal = deal;
	}
	
} 