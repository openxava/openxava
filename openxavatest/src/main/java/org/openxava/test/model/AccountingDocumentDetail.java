package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.*;
import org.openxava.model.*;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class AccountingDocumentDetail extends Identifiable {
	
	@Required
	@Column(length = 40)
	private String description;
	
	@ManyToOne
	@ReadOnly @NoFrame
	private AccountingDocument document;
	
	
	@ManyToMany
	private Collection<AccountingInvoice> invoices;


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public AccountingDocument getDocument() {
		return document;
	}


	public void setDocument(AccountingDocument document) {
		this.document = document;
	}


	public Collection<AccountingInvoice> getInvoices() {
		return invoices;
	}


	public void setInvoices(Collection<AccountingInvoice> invoices) {
		this.invoices = invoices;
	}

}
