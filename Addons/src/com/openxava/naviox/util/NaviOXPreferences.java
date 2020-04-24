package com.openxava.naviox.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.util.*;
import org.openxava.validators.*;

import com.openxava.naviox.impl.*;

/**
 * To get the preferences from naviox.properties
 * 
 * The properties encryptPassword, storePasswordAsHex and all ldap properties are obtained
 * directly from User class for not creating a compilation dependency between User.java and naviox.jar. 
 * 
 * @author Javier Paniza
 */
public class NaviOXPreferences {

	private final static String PROPERTIES_FILE = "naviox.properties";
	private static Log log = LogFactory.getLog(NaviOXPreferences.class);
	private static NaviOXPreferences instance;
	private boolean testingAutologin = false;
	private boolean testingNotShowOrganizationOnSignIn = false;
	private boolean testingNotShowListOfOrganizationsOnSignIn = false; 
	private Properties properties;

	private NaviOXPreferences() {
	}

	public static NaviOXPreferences getInstance() {
		if (instance == null) {
			instance = new NaviOXPreferences();
		}
		return instance;
	}
	
	public static void startTestingAutologin() { 
		getInstance().testingAutologin = true;
	}
	
	public static void stopTestingAutologin() { 
		getInstance().testingAutologin = false;
	}
	
	public static void startTestingNotShowOrganizationOnSignIn() {
		getInstance().testingNotShowOrganizationOnSignIn = true;
	}
	
	public static void stopTestingNotShowOrganizationOnSignIn() {
		getInstance().testingNotShowOrganizationOnSignIn = false;
	}
	
	public static void startTestingNotShowListOfOrganizationsOnSignIn() { 
		getInstance().testingNotShowListOfOrganizationsOnSignIn = true;
	}
	
	public static void stopTestingNotShowListOfOrganizationsOnSignIn() { 
		getInstance().testingNotShowListOfOrganizationsOnSignIn = false;
	}
	
	private Properties getProperties() {
		if (properties == null) {
			PropertiesReader reader = new PropertiesReader(
					XavaPreferences.class, PROPERTIES_FILE);
			try {
				properties = reader.get();
			} catch (IOException ex) {
				log.error(XavaResources.getString("properties_file_error",
						PROPERTIES_FILE), ex);
				properties = new Properties();
			}
		}
		return properties;
	}
		
	public String getAutologinUser() { 
		if (testingAutologin) return "user"; // Only for testing purposes
		return getProperties().getProperty("autologinUser", "").trim();
	}
	
	public String getAutologinPassword() {
		if (testingAutologin) return "user"; // Only for testing purposes
		return getProperties().getProperty("autologinPassword", "").trim();
	}
	
	/** @since 5.4 */
	public String getInitialPasswordForAdmin() { 
		return getProperties().getProperty("initialPasswordForAdmin", "admin").trim();
	}

	/** @since 5.6 */
	public String getAllModulesNamesProviderClass() { 
		return getProperties().getProperty("allModulesNamesProviderClass", AllModulesNamesProvider.class.getName()).trim();
	}

	
	private String getCreateSchema() {
		return getProperties().getProperty("createSchema", "CREATE SCHEMA ${schema}").trim();
	}
	
	/**
	 * @since 5.2
	 */
	public String getCreateSchema(String database) {
		return getProperties().getProperty("createSchema." + database, getCreateSchema()).trim();
	}
	
	private String getDropSchema() { 
		return getProperties().getProperty("dropSchema", "DROP SCHEMA ${schema} CASCADE").trim();
	}
	
	/**
	 * @since 5.7.1
	 */
	public String getDropSchema(String database) { 
		return getProperties().getProperty("dropSchema." + database, getDropSchema()).trim();
	}

	
	/**
	 * @since 5.6
	 */
	public boolean isShowOrganizationOnSignIn() { 
		if (testingNotShowOrganizationOnSignIn) return false; // Only for testing purposes
		return "true".equalsIgnoreCase(getProperties().getProperty("showOrganizationOnSignIn", "true").trim());
	}

	/**
	 * @since 6.3
	 */
	public boolean isShowListOfOrganizationsOnSignIn() { 
		if (testingNotShowListOfOrganizationsOnSignIn) return false; // Only for testing purposes
		return "true".equalsIgnoreCase(getProperties().getProperty("showListOfOrganizationsOnSignIn", "true").trim());
	}

		
	/**
	 * @since 5.3
	 */
	public boolean isStartInLastVisitedModule() { 
		return "true".equalsIgnoreCase(getProperties().getProperty("startInLastVisitedModule", "true").trim());
	}

	/**
	 * @since 6.3.2
	 */	
	public String getInitialModule() { 
		return getProperties().getProperty("initialModule", "FirstSteps").trim();
	}
		
	/**
	 * 
	 * @since 5.9
	 */
	public String getFixModulesOnTopMenu() { 
		return getProperties().getProperty("fixModulesOnTopMenu", "").trim();
	}

	
	/**
	 * @since 5.5
	 */
	public boolean isShowApplicationName() { 
		return "true".equalsIgnoreCase(getProperties().getProperty("showApplicationName", "true").trim());
	}
	
	/**
	 * @since 5.7
	 */
	public boolean isUpdateNaviOXTablesInOrganizationsOnStartup() { 
		return "true".equalsIgnoreCase(getProperties().getProperty("updateNaviOXTablesInOrganizationsOnStartup", "true").trim());
	}

	/**
	 * @since 5.8
	 */
	public boolean isShowModulesMenuWhenNotLogged() { 
		return "true".equalsIgnoreCase(getProperties().getProperty("showModulesMenuWhenNotLogged", "true").trim());
	}
		
	/** @since 6.0 */
	public String getEmailValidatorForSignUpClass() { 
		return getProperties().getProperty("emailValidatorForSignUpClass", EmailValidator.class.getName()).trim();
	}
		
}
