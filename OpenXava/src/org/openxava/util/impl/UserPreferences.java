package org.openxava.util.impl;

import java.io.*;

import java.util.*;
import java.util.prefs.*;

import org.apache.commons.io.*;
import org.apache.commons.logging.*;
import org.openxava.util.*;

/**
 * Implementation of Java Preferences for OX applications. <p>
 * 
 * It's not intended for use at global preferences for the JVM
 * (that is as value for system property <code>
 * java.util.prefs.PreferencesFactory</code>), but for obtaining
 * it from {@link org.openxava.util.Users#getCurrentPreferences}.<br> 
 * 
 * @author Javier Paniza
 */

public class UserPreferences extends AbstractPreferences {

	private static Log log = LogFactory.getLog(UserPreferences.class); 
	private final static String ANONIMOUS = "__ANONIMOUS__";
	private static Map preferencesByUser; 
	private String userName;
	private String name;
	private String fileName;
	private Map children;
	private Properties properties;
	
	protected UserPreferences(AbstractPreferences parent, String name, String userName) {
		super(parent, name);
		this.userName = userName;
		this.name = name;
	}
	
	public static Preferences getForUser(String userName) throws BackingStoreException {
		if (userName == null) userName = ANONIMOUS;
		if (preferencesByUser == null) preferencesByUser = new HashMap();
		UserPreferences preferences = (UserPreferences) preferencesByUser.get(userName);
		if (preferences == null) {			
			preferences = new UserPreferences(null, "", userName);						
			preferences.syncSpi();						
			preferencesByUser.put(userName, preferences);
		}		
		return preferences;
	}
	
	/**
	 * @since 5.8
	 */
	public static void removeAll() throws BackingStoreException { 
		try {
			FileUtils.deleteDirectory(new File(Files.getOpenXavaBaseDir())); 
		}
		catch (Exception ex) {
			log.error(XavaResources.getString("remove_all_user_preferences_error"), ex);
			throw new BackingStoreException(XavaResources.getString("remove_all_user_preferences_error")); 
		}
		preferencesByUser = null;		
	}

	protected AbstractPreferences childSpi(String name) {
		if (children == null) children = new HashMap();
		UserPreferences child = (UserPreferences) children.get(name);
		if (child == null) {
			child = new UserPreferences(this, name, userName);
			try {
				child.syncSpi();
			}
			catch (BackingStoreException ex) {
				throw new RuntimeException(ex);
			}
			children.put(name, child);
		}
		return child;
	}

	protected String[] childrenNamesSpi() throws BackingStoreException {
		// It takes 0 milliseconds even with more than 20 entries, so we don't cache
		File dir = new File(Strings.noLastTokenWithoutLastDelim(getFileName(), "."));
		String [] files = dir.list(); 
		if (files == null) return new String[0];			
		String [] children = new String[files.length];
		for (int i=0; i< files.length; i++) {
			children[i] = Strings.noLastTokenWithoutLastDelim(files[i], ".");
		}
		return children;
	}

	protected void flushSpi() throws BackingStoreException {
		if (properties == null) return;
		try {			
			createFileIfNotExist();
			FileOutputStream stream = new FileOutputStream(getFileName());
			properties.store(stream, "OpenXava preferences. User: " + userName + ". Node: " + name);
			stream.close();
		} 
		catch (Exception ex) {
			throw new BackingStoreException(ex);
		}
	}

	private void createFileIfNotExist() throws Exception {
		Files.createFileIfNotExist(getFileName()); 
	}

	private String getFileName() {		
		if (fileName == null) {
			fileName = Files.getOpenXavaBaseDir() + userName + "__" + getQualifiedName() + ".properties"; 
		}
		return fileName;
	}
	
	private String getQualifiedName() {
		if (parent() == null || parent().name().trim().equals("")) return name;
		return parent().name() + "/" + name; 
	}

	protected String getSpi(String key) {
		if (properties == null) return null;
		return properties.getProperty(key);
	}

	protected String[] keysSpi() throws BackingStoreException {
		if (properties == null) return new String[0];
		return XCollections.toStringArray(properties.keySet());		
	}

	protected void putSpi(String key, String value) {
		if (properties == null) properties = new Properties();
		properties.put(key, value);
	}

	protected void removeNodeSpi() throws BackingStoreException {		
		File f = new File(getFileName());
		if (!f.delete()) {
			throw new BackingStoreException(XavaResources.getString("preferences_node_not_removed")); 
		}
		preferencesByUser = null; 
	}

	protected void removeSpi(String key) {
		if (properties != null) properties.remove(key);
	}

	protected void syncSpi() throws BackingStoreException {		
		try {
			Properties newProperties = new Properties();
			FileInputStream stream = new FileInputStream(getFileName());
			newProperties.load(stream);
			stream.close();
			if (properties == null) properties = newProperties;
			else properties.putAll(newProperties);
		} 
		catch (FileNotFoundException ex) {
			// If file does not exist just we don't load it
		}
		catch (Exception ex) {
			throw new BackingStoreException(ex);
		}		
	}

}
