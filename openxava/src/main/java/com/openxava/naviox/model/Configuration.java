/*
 * NaviOX. Navigation and security for OpenXava applications.
 * Copyright 2014 Javier Paniza Lucas
 *
 * License: Apache License, Version 2.0.	
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */

package com.openxava.naviox.model;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.apache.commons.logging.*;
import org.openxava.annotations.*;
import org.openxava.util.*;

import com.openxava.naviox.impl.*;
import com.openxava.naviox.util.*;

/**
 *
 * @since 5.4
 * @author Javier Paniza
 */

@MappedSuperclass
public class Configuration implements java.io.Serializable {
	
	private static Log log = LogFactory.getLog(Configuration.class);
	private static Configuration instance;
	
	@Id @Hidden
	private int id;
	
	@Max(30)
	private int passwordMinLength;
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@Column(name="forceLetterAndNumbersInPasswd", columnDefinition="varchar(1) default 'N' not null") 
	private boolean forceLetterAndNumbersInPassword;
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@Column(columnDefinition="varchar(1) default 'N' not null") 
	private boolean recentPasswordsNotAllowed; 
	
	@Max(999)
	private int forceChangePasswordDays;
	
	@Max(999)
	private int lockSessionMinutes;
	
	@Max(999)
	private int loginAttemptsBeforeLocking; 
	
	@Max(999)
	@Column(name="inactiveDaysBeforeDisUser") 
	private int inactiveDaysBeforeDisablingUser;
	
	/** @since 7.4 */
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@Column(columnDefinition="varchar(1) default 'N' not null")	
	private boolean allowSeveralSessionsPerUser; // tmr Cambiar schema-update.sql. Actualizar pantallazo doc. i18n 	
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@Column(columnDefinition="varchar(1) default 'Y' not null")
	private boolean guestCanCreateAccount;  
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@Column(name="guestCanCreateAccountInOrgs", columnDefinition="varchar(1) default 'Y' not null") 
	private boolean guestCanCreateAccountInOrganizations;  
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@Column(name="sharedUsersBetweenOrgs", columnDefinition="varchar(1) default 'N' not null") 
	private boolean sharedUsersBetweenOrganizations;
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@Column(columnDefinition="varchar(1) default 'N' not null")
	private boolean useEmailAsUserName; 
	
	/** @since 6.6 */
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@Column(columnDefinition="varchar(1) default 'N' not null")	
	private boolean caseSensitiveUserName;
	
	@org.hibernate.annotations.Type(type="org.hibernate.type.YesNoType")
	@Column(columnDefinition="varchar(1) default 'N' not null")
	private boolean privacyPolicyOnSignUp; 
	
	

	@Hidden
	public int getLockSessionMilliseconds() {  
		// Negative minutes are treated as seconds, a trick for testing purposes
		return lockSessionMinutes > 0?lockSessionMinutes * 60000:lockSessionMinutes * -1000; 
	}
	
	
	public static Configuration getInstance() {
		if (instance == null) {
			try {
				IConfigurationFactory factory = (IConfigurationFactory) Class.forName(NaviOXPreferences.getInstance().getConfigurationFactoryClass()).newInstance();
				instance = factory.create();
			} 
			catch (Exception ex) {
				log.warn(XavaResources.getString("factory_create_error", "Configuration"), ex);
				throw new XavaException("factory_create_error", "Configuration");
			}
		}
		return instance;
	}
	
	public static void resetInstance() {
		instance = null;
	}
	
	@PrePersist
	private void prePersist() {
		id = 1;
	}

	public int getPasswordMinLength() {
		return passwordMinLength;
	}

	public void setPasswordMinLength(int passwordMinLength) {
		this.passwordMinLength = passwordMinLength;
	}

	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}

	public boolean isForceLetterAndNumbersInPassword() {
		return forceLetterAndNumbersInPassword;
	}

	public void setForceLetterAndNumbersInPassword(
			boolean forceLetterAndNumbersInPassword) {
		this.forceLetterAndNumbersInPassword = forceLetterAndNumbersInPassword;
	}

	public boolean isRecentPasswordsNotAllowed() {
		return recentPasswordsNotAllowed;
	}

	public void setRecentPasswordsNotAllowed(boolean recentPasswordsNotAllowed) {
		this.recentPasswordsNotAllowed = recentPasswordsNotAllowed;
	}

	public int getForceChangePasswordDays() {
		return forceChangePasswordDays;
	}

	public void setForceChangePasswordDays(int forceChangePasswordDays) {
		this.forceChangePasswordDays = forceChangePasswordDays;
	}
	
	public int getLockSessionMinutes() {
		return lockSessionMinutes;
	}

	public void setLockSessionMinutes(int lockSessionMinutes) {
		this.lockSessionMinutes = lockSessionMinutes;
	}


	public int getLoginAttemptsBeforeLocking() {
		return loginAttemptsBeforeLocking;
	}


	public void setLoginAttemptsBeforeLocking(int loginAttemptsBeforeLocking) {
		this.loginAttemptsBeforeLocking = loginAttemptsBeforeLocking;
	}


	public int getInactiveDaysBeforeDisablingUser() {
		return inactiveDaysBeforeDisablingUser;
	}


	public void setInactiveDaysBeforeDisablingUser(
			int inactiveDaysBeforeDisablingUser) {
		this.inactiveDaysBeforeDisablingUser = inactiveDaysBeforeDisablingUser;
	}


	public boolean isGuestCanCreateAccount() {
		return guestCanCreateAccount;
	}


	public void setGuestCanCreateAccount(boolean guestCanCreateAccount) {
		this.guestCanCreateAccount = guestCanCreateAccount;
	}
	
	public boolean isGuestCanCreateAccountInOrganizations() {
		return guestCanCreateAccountInOrganizations;
	}


	public void setGuestCanCreateAccountInOrganizations(boolean guestCanCreateAccountInOrganizations) {
		this.guestCanCreateAccountInOrganizations = guestCanCreateAccountInOrganizations;
	}


	public boolean isSharedUsersBetweenOrganizations() {
		return sharedUsersBetweenOrganizations;
	}


	public void setSharedUsersBetweenOrganizations(boolean sharedUsersBetweenOrganizations) {
		this.sharedUsersBetweenOrganizations = sharedUsersBetweenOrganizations;
	}


	public boolean isUseEmailAsUserName() {
		return useEmailAsUserName;
	}


	public void setUseEmailAsUserName(boolean useEmailAsUserName) {
		this.useEmailAsUserName = useEmailAsUserName;
	}


	public boolean isPrivacyPolicyOnSignUp() {
		return privacyPolicyOnSignUp;
	}


	public void setPrivacyPolicyOnSignUp(boolean privacyPolicyOnSignUp) {
		this.privacyPolicyOnSignUp = privacyPolicyOnSignUp;
	}


	public boolean isCaseSensitiveUserName() {
		return caseSensitiveUserName;
	}


	public void setCaseSensitiveUserName(boolean caseSensitiveUserName) {
		this.caseSensitiveUserName = caseSensitiveUserName;
	}


	public boolean isAllowSeveralSessionsPerUser() {
		return allowSeveralSessionsPerUser;
	}


	public void setAllowSeveralSessionsPerUser(boolean allowSeveralSessionsPerUser) {
		this.allowSeveralSessionsPerUser = allowSeveralSessionsPerUser;
	}

}
