package org.openxava.util;

import java.io.*;
import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang3.*;
import org.apache.commons.logging.*;
import org.apache.commons.validator.routines.*;
import org.openxava.application.meta.*;
import org.openxava.component.*;
import org.openxava.jpa.*;
import org.openxava.tab.meta.*;
import org.openxava.util.impl.*;

/**
 * 
 * @author Javier Paniza
 */

public class EmailNotifications {
	
	private static Log log = LogFactory.getLog(EmailNotifications.class); 
	
	private static class ModuleInfo {
		
		private String application;
		private String applicationLabel;
		private String module;
		private String moduleModel;
		private String moduleLabel;
		private String url;
		
		public ModuleInfo(String application, String module, String url) {
			this.application = application;
			this.module = module;
			this.url = url;
		}
		
		private void initModuleInfo() {
			try {
				MetaModule metaModule = MetaApplications.getMetaApplication(getCurrentApplication()).getMetaModule(getCurrentModule());
				moduleModel = metaModule.getModelName();
				moduleLabel = metaModule.getLabel();
			}
			catch (Exception ex) {
				moduleLabel = Labels.get(getCurrentModule());
				log.warn(XavaResources.getString("module_info_email_notifications"), ex);
			} 			
		}

		public String getApplication() {
			return application;
		}
		
		public String getApplicationLabel() {
			if (applicationLabel == null) {
				applicationLabel = Labels.get(application);
			}
			return applicationLabel;
		}
		
		public String getModule() {
			return module;
		}
				
		public String getModuleModel() {
			if (moduleModel == null) {
				initModuleInfo();
			}
			return moduleModel;			
		}
		
		public void setModuleModel(String moduleModel) {
			if (moduleModel.equals(this.moduleModel)) return;
			this.module = moduleModel;
			this.moduleModel = null;
			this.moduleLabel = null;
			this.url = Strings.noLastToken(this.url, "/") + this.module;
		}

		public String getModuleLabel() {
			if (moduleLabel == null) {
				initModuleInfo(); 
			}
			return moduleLabel;
		}
		
		public String getURL() {
			return url;
		}
	}
	
	final private static ThreadLocal<ModuleInfo> currentModuleInfo = new ThreadLocal<ModuleInfo>();
	
	private static String fileName = null;
	
	public static void setModuleInfo(String applicationName, String moduleName, String moduleURL) {
		ModuleInfo moduleInfo = new ModuleInfo(applicationName, moduleName, moduleURL);
		currentModuleInfo.set(moduleInfo);
	}
	
	public static void subscribeToModule(String email, String module) { 
		EntityManager manager = createManager();
		try {
			subscribeToModule(manager, email, module);
			commit(manager);
		}
		catch (RuntimeException ex) {
			rollback(manager);
			throw ex;
		}
	}
	
	public static void subscribeCurrentUserToModule(String module) {
		subscribeToModule(getCurrentUserEmail(), module);
	}
	
	private static void subscribeToModule(EntityManager manager, String email, String module) { 
		if (!isEnabled()) return;
		if (isSubscribedCurrentUserToModule(manager, module)) return; // To avoid duplicate key error when an entity is removed and added again with the same key 
		manager.persist(new EmailSubscription(email, module));
	}
	
	public static void subscribeCurrentUserToEntity(String module, Map key) { 
		subscribeCurrentUserToModule(module + keyToString(key));
	}
	
	public static void subscribeToEntity(String email, String module, Map key) { 
		subscribeToModule(email, module + keyToString(key));
	}
	
	private static void subscribeCurrentUserToEntity(EntityManager manager, String module, Map key) { 
		subscribeToModule(manager, getCurrentUserEmail(), module + keyToString(key));
	}

	public static void unsubscribeCurrentUserFromModule(String module) {
		if (!isEnabled()) return;
		EntityManager manager = createManager();
		try {
			EmailSubscription.remove(manager, getCurrentUserEmail(), module);
			commit(manager);
		}
		catch (RuntimeException ex) {
			rollback(manager);
			throw ex;
		}
	}
	
	private static void commit(EntityManager manager) {
		manager.getTransaction().commit();
		manager.close();
	}
	
	private static void rollback(EntityManager manager) {
		manager.getTransaction().rollback();
		manager.close();
	}

	private static EntityManager createManager() {
		EntityManager manager = XPersistence.createManager();
		manager.getTransaction().begin();
		return manager;
	}

	public static int unsubscribeAllEntitiesOfModule(String email, String module) { 
		if (!isEnabled()) return 0;
		EntityManager manager = createManager();
		try {
			int result = EmailSubscription.removeByEmailAndModuleLike(manager, email, module + "%");
			commit(manager);
			return result;
		}
		catch (RuntimeException ex) {
			rollback(manager);
			throw ex;
		}
	}
	
	public static boolean unsubscribeFromEntity(String email, String module, String key) {
		return unsubscribeFromModule(email, module + key);
	}
	
	public static boolean unsubscribeFromModule(String email, String module) {
		if (!isEnabled()) return false;
		EntityManager manager = createManager();
		try {
			boolean result = EmailSubscription.remove(manager, email, module);
			commit(manager);
			return result;
		}
		catch (RuntimeException ex) {
			rollback(manager);
			throw ex;
		}

	}
	
	public static boolean isSubscribedCurrentUserToModule(String module) {
		if (!isEnabled()) return false;
	
		EntityManager manager = createManager();
		try {
			boolean result = isSubscribedCurrentUserToModule(manager, module);
			commit(manager);
			return result;
		}
		catch (RuntimeException ex) {
			rollback(manager);
			log.warn(XavaResources.getString("email_subscriptions_persistence_problem"), ex);
			return false;
		}
	}
	
	private static boolean isSubscribedCurrentUserToModule(EntityManager manager, String module) {
		return EmailSubscription.find(manager, getCurrentUserEmail(), module) != null;
	}
	
	public static boolean isEnabled(String module) {
		try {
			if (!isEnabled()) return false;
			MetaModule metaModule = MetaApplications.getMetaApplication(getCurrentApplication()).getMetaModule(module);
			String tabName = metaModule.getTabName();
			MetaComponent component = MetaComponent.get(metaModule.getModelName());
			MetaTab tab = component.getMetaTab(tabName);
			return tab != null && Is.emptyString(tab.getBaseCondition());
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("email_notifications_enabled_problem"), ex);
			return false;
		}
	}
	
	public static boolean currentUserHasEmail() { 
		return getCurrentUserEmail() != null;
	}
	
	private static boolean isEnabled() {
		return XavaPreferences.getInstance().getAccessTrackerProvidersClasses().contains(EmailNotificationsAccessTrackerProvider.class.getName());
	}
	
	private static String getCurrentApplicationLabel() {
		return currentModuleInfo.get().getApplicationLabel();
	}
	
	private static String getCurrentModuleLabel() {
		return currentModuleInfo.get().getModuleLabel();
	}
	
	private static String getCurrentApplication() {
		return currentModuleInfo.get().getApplication();
	}
	
	private static String getCurrentModule() {
		return currentModuleInfo.get().getModule();
	}
	
	private static String getCurrentModuleModel() {
		return currentModuleInfo.get().getModuleModel();
	}
	
	private static void setCurrentModuleModel(String moduleModel) {
		currentModuleInfo.get().setModuleModel(moduleModel);
	}
	
	private static String getCurrentModuleURL() {
		return currentModuleInfo.get().getURL();
	}
	
	private static String getCurrentUserEmail() { 
		String email = Users.getCurrentUserInfo().getEmail();
		if (!Is.emptyString(email)) return email;
		String user = Users.getCurrent();
		if (EmailValidator.getInstance().isValid(user)) return user;
		return null;
	}
	
	static void notifyCreated(String modelName, Map key) {
		setCurrentModuleModel(modelName);
		String subject = XavaResources.getString("email_notification_created_subject", 
       		getCurrentApplicationLabel(), getCurrentModuleLabel());
		String permalink = toPermalink(getCurrentModuleURL(), key);
		String content =XavaResources.getString("email_notification_created_content", 
       		Users.getCurrent(),	permalink);

		String logSuffix = ", permalink=" + permalink;
		EntityManager manager = createManager();
		try {
			notifyByModule(manager, "CREATED", subject, content, logSuffix);
			if (currentUserHasEmail()) { 
				subscribeCurrentUserToEntity(manager, modelName, key);
			}
			commit(manager);
		}
		catch (RuntimeException ex) {
			rollback(manager);
			throw ex;
		}
	}
	
	static void notifyModified(String modelName, Map key, Map<String, Object> oldChangedValues, Map<String, Object> newChangedValues) {
		setCurrentModuleModel(modelName);
		
		StringBuffer changes = new StringBuffer("<ul>"); 
		for (String property: oldChangedValues.keySet()) {
			changes.append("<li><b>");
			changes.append(Labels.getQualified(property));
			changes.append("</b>: ");
			changes.append(Strings.toString(oldChangedValues.get(property)));
			changes.append(" --> ");
			changes.append(Strings.toString(newChangedValues.get(property)));
			changes.append("</li>");
		}		
		changes.append("</ul>");
		String permalink = toPermalink(getCurrentModuleURL(), key);
		String subject = XavaResources.getString("email_notification_modified_subject", 
       		getCurrentApplicationLabel(), getCurrentModuleLabel());
		String content = XavaResources.getString("email_notification_modified_content", 
       		Users.getCurrent(), permalink, changes.toString());
		
		String logSuffix = ", permalink=" + permalink + ", changes=" + changes;
		Set notifiedEmails = notifyByModule("MODIFIED", subject, content, logSuffix);
		notifyByEntity("MODIFIED", key, subject, content, notifiedEmails);
	}

	static void notifyRemoved(String modelName, Map key) { 
		setCurrentModuleModel(modelName);
		String subject = XavaResources.getString("email_notification_removed_subject", 
       		getCurrentApplicationLabel(), getCurrentModuleLabel());
		String content = XavaResources.getString("email_notification_removed_content", 
       		Users.getCurrent(),	getCurrentModuleLabel(), getCurrentModuleURL(), key);
		String logSuffix = ", url=" + getCurrentModuleURL() + ", key=" + key; 
		Set notifiedEmails = notifyByModule("REMOVED", subject, content, logSuffix);
		notifyByEntity("REMOVED", key, subject, content, notifiedEmails);
		
	}
	
	private static Set notifyByModule(String type, String subject, String content, String logSuffix) {
		EntityManager manager = createManager();
		try {
			Set result = notifyByModule(manager, type, subject, content, logSuffix);
			commit(manager);
			return result;
		}
		catch (RuntimeException ex) {
			rollback(manager);
			throw ex;
		}
	}
	
	private static Set notifyByModule(EntityManager manager, String type, String subject, String content, String logSuffix) {
		String contentForModule = decorateModuleUnsubscribe(content); 
		Set notifiedEmails = new HashSet();
		String currentUserEmail = getCurrentUserEmail();
		for (EmailSubscription s: EmailSubscription.findByModule(manager, getCurrentModule())) {
			if (s.getEmail().equals(currentUserEmail)) continue;
           	Emails.sendInBackground(s.getEmail(), subject, contentForModule);
           	notifiedEmails.add(s.getEmail());
           	log(type + ": email=" + s.getEmail() + ", user=" + Users.getCurrent() + 
           		", application=" + getCurrentApplicationLabel() + ", module=" + getCurrentModuleLabel() + 
           		logSuffix);
		}
		return notifiedEmails;
	}
	
	private static void notifyByEntity(String type, Map key, String subject, String content, Set notifiedEmails) {
		EntityManager manager = createManager();
		try {
			notifyByEntity(manager, type, key, subject, content, notifiedEmails);
			commit(manager);
		}
		catch (RuntimeException ex) {
			rollback(manager);
			throw ex;
		}
	}
	
	private static void notifyByEntity(EntityManager manager, String type, Map key, String subject, String content, Set notifiedEmails) {
		String unsuscribeURL = toBaseURL(getCurrentModuleURL()) + "/xava/unsubscribe.jsp?email=";
		String skey = keyToString(key);
		String currentUserEmail = getCurrentUserEmail();
		for (EmailSubscription s: EmailSubscription.findByModule(manager, getCurrentModule() + keyToString(key))) {
			if (notifiedEmails.contains(s.getEmail())) continue;
			if (s.getEmail().equals(currentUserEmail)) continue;
			String unsubscribeAllURL = unsuscribeURL + s.getEmail() + "&module=" + getCurrentModule();
			String unsubscribeOneURL = unsubscribeAllURL + "&key=" + skey;
			String contentForRecord = decorateRecordUnsubscribe(content, key, unsubscribeOneURL, unsubscribeAllURL);
			Emails.sendInBackground(s.getEmail(), subject, contentForRecord); 
           	log(type + ": email=" + s.getEmail() + ", user=" + Users.getCurrent() + 
           		", application=" + getCurrentApplicationLabel() + ", module=" + getCurrentModuleLabel() + 
           		", unsubscribeAllURL=" + unsubscribeAllURL + ", unsubscribeOneURL=" + unsubscribeOneURL);
		}
	}
	

	
	private static String toBaseURL(String moduleURL) {
		int startingPoint = moduleURL.contains("/" + getCurrentApplication() + "/")?4:3;
		int idx = StringUtils.ordinalIndexOf(moduleURL, "/", startingPoint); 
		return moduleURL.substring(0, idx);
	}

	private static String decorateModuleUnsubscribe(String content) {
		return content + "<br><br>" +
			XavaResources.getString("email_notification_module_unsuscribe", 
				getCurrentApplicationLabel(), getCurrentModuleLabel(), getCurrentModuleURL());	
	}
	
	private static String decorateRecordUnsubscribe(String content, Map key, String unsubscribeOneURL, String unsubscribeAllURL) {
		return content + "<br><br>" +
			XavaResources.getString("email_notification_record_unsuscribe",  
				getCurrentApplicationLabel(), getCurrentModuleLabel(), key, unsubscribeOneURL, unsubscribeAllURL);
	}
	
	private static String toPermalink(String moduleURL, Map key) {
		if (key == null || key.size() == 0) return moduleURL;
		Object id = key.values().iterator().next();
		return moduleURL + "?detail=" + id;
	}

	
	private static void log(String line) {
		try {
			createFileIfNotExist();
			FileOutputStream f = new FileOutputStream(getFileName(), true); 
			PrintStream p = new PrintStream(f);
			p.println(line);
			p.close();
			f.close();
		}
		catch (Exception ex) {
			log.warn(XavaResources.getString("email_notifications_log_failed"), ex);
		}
	}
		
	private static void createFileIfNotExist() throws Exception { 
		Files.createFileIfNotExist(getFileName()); 	
	}

	private static String getFileName() {		
		if (fileName == null) {
			fileName = Files.getOpenXavaBaseDir() + "email-notifications.log"; 			
		}
		return fileName;
	}
	
	private static String keyToString(Map key) {
		if (key.size() == 1) return "::" + key.values().iterator().next().toString();
		List names = new ArrayList(key.keySet());
		Collections.sort(names);
		StringBuffer sb = new StringBuffer();
		for (Object name: names) {
			sb.append("::");
			sb.append(key.get(name));
		}
		return sb.toString();
	}

}
