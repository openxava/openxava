package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;

/**
 * Example of complex dependencies within properties with stereotypes,
 * based on editors. <p>
 * 
 * @author Javier Paniza
 */

@Entity
@View( members= 
	"number;" +
	"description;" +
	"detail {" +
	"	family;" +
	"	detail;" +
	"	additionalDetails; " +
	"}" +
	"invoice {" +
	"	invoice;" +
	"}"
)
public class Service {

	@Id @Column(length=3)
	private int number;
	
	@Column(length=30) 
	private String description;
	
	 @Stereotype("FAMILY")
	private int family;
	
	@Embedded
	private ServiceDetail detail;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@Action("Service.seeMessage")
	private ServiceInvoice invoice; 
	
	@OneToMany (mappedBy="service", cascade=CascadeType.REMOVE)
	private Collection<AdditionalDetail> additionalDetails;
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getFamily() {
		return family;
	}

	public void setFamily(int family) {
		this.family = family;
	}

	public ServiceDetail getDetail() {
		return detail;
	}

	public void setDetail(ServiceDetail detail) {
		this.detail = detail;
	}

	public Collection<AdditionalDetail> getAdditionalDetails() {
		return additionalDetails;
	}

	public void setAdditionalDetails(Collection<AdditionalDetail> additionalDetails) {
		this.additionalDetails = additionalDetails;
	}

	public ServiceInvoice getInvoice() {
		return invoice;
	}

	public void setInvoice(ServiceInvoice invoice) {
		this.invoice = invoice;
	}

}
