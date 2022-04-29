package org.openxava.test.model;

import javax.persistence.*;
import javax.persistence.Entity;

/**
 * 
 * @author Javier Paniza
 */

@Entity
public class SlotMachineAccount {

	@Id
	@Column(length=7) 
	private String account;
	
	@Column(length=250)
	private String accountName;


	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
			
}
