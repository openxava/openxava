package org.openxava.test.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.openxava.annotations.*;
import org.openxava.test.actions.*;

/**
 * Representing a second level embeddable class as detailed in bug 3047205 
 * @author Federico Alcantara
 *
 */
@Embeddable
public class PhoneNumber implements Serializable {
	private static final long serialVersionUID = 1L;

	@OnChange(OnChangePhoneAreaCodeAction.class) 
	private int phoneAreaCode;
	private String phoneDigits;
	private String phoneExtension;
	/**
	 * @return the phoneAreaCode
	 */
	public int getPhoneAreaCode() {
		return phoneAreaCode;
	}
	/**
	 * @param phoneAreaCode the phoneAreaCode to set
	 */
	public void setPhoneAreaCode(int phoneAreaCode) {
		this.phoneAreaCode = phoneAreaCode;
	}
	/**
	 * @return the phoneDigits
	 */
	public String getPhoneDigits() {
		return phoneDigits;
	}
	/**
	 * @param phoneDigits the phoneDigits to set
	 */
	public void setPhoneDigits(String phoneDigits) {
		this.phoneDigits = phoneDigits;
	}
	/**
	 * @return the phoneExtension
	 */
	public String getPhoneExtension() {
		return phoneExtension;
	}
	/**
	 * @param phoneExtension the phoneExtension to set
	 */
	public void setPhoneExtension(String phoneExtension) {
		this.phoneExtension = phoneExtension;
	}

}
