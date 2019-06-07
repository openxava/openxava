package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

/**
 * tmp
 * 
 * @author Javier Paniza
 */

@Entity
public class AccountingInvoice extends AccountingDocument {
	
	@ManyToMany(mappedBy="invoices")
	private Collection<AccountingDocumentDetail> receipts;

	public Collection<AccountingDocumentDetail> getReceipts() {
		return receipts;
	}

	public void setReceipts(Collection<AccountingDocumentDetail> receipts) {
		this.receipts = receipts;
	}

}
