package org.openxava.test.model;

import java.util.*;

import javax.persistence.*;

import org.openxava.annotations.Hidden;

/**
 * 
 * @author Javier Paniza
 */
@Entity
public class PerishableArticle extends Article {
	
	@Hidden // tmr
	private Date expirationDate;

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

}
