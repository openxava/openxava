package org.openxava.util;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import org.apache.commons.logging.*;
import org.openxava.view.meta.*;

/**
 * @author Javier Paniza
 */
public class XavaPreferences {

	private final static String FILE_PROPERTIES = "xava.properties";
	private final static String JAVA_LOGGING_LEVEL_DEFAULT_VALUE = "INFO";
	private static Log log = LogFactory.getLog(XavaPreferences.class);

	private static XavaPreferences instance;

	private Properties properties;

	private boolean duplicateComponentWarningsLoaded = false;
	private boolean duplicateComponentWarnings = false;
	private int maxSizeForTextEditor;
	private Level javaLoggingLevel;
	private Level hibernateJavaLoggingLevel;
	private int pageRowCount;
	private int rowActionsPopupThreshold = -1; 
	
	private int defaultLabelFormat = -1;

	private XavaPreferences() {
	}

	public static XavaPreferences getInstance() {
		if (instance == null) {
			instance = new XavaPreferences();
		}
		return instance;
	}

	private Properties getProperties() {
		if (properties == null) {
			PropertiesReader reader = new PropertiesReader(
					XavaPreferences.class, FILE_PROPERTIES);
			try {
				properties = reader.get();
			} catch (IOException ex) {
				log.error(XavaResources.getString("properties_file_error",
						FILE_PROPERTIES), ex);
				properties = new Properties();
			}
		}
		return properties;
	}

	public boolean isReadOnlyAsLabel() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"readOnlyAsLabel", "false").trim());
	}
	
	/**
	 * @since 5.8
	 */
	public boolean isShowDefaultActionInBottom() { 
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"showDefaultActionInBottom", "true").trim());
	}

	public boolean isShowCountInList() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"showCountInList", "true").trim());
	}

	public String getSMTPHost() {
		return getProperties().getProperty("smtpHost");
	}

	public int getSMTPPort() {
		return Integer.parseInt(getProperties().getProperty("smtpPort"));
	}

	public String getSMTPUserID() {
		return getProperties().getProperty("smtpUserId");
	}

	public String getSMTPUserPassword() {
		return getProperties().getProperty("smtpUserPassword");
	}
		
	/** @since 6.6 */
	public String getMapsTileProvider() { 
		return getProperties().getProperty("mapsTileProvider", "https://b.tile.opentopomap.org/{z}/{x}/{y}.png");
	}

	/** @since 6.6 */
	public String getMapsAttribution() { 
		return getProperties().getProperty("mapsAttribution", "Map data &copy; <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors, Imagery &copy; <a href='https://opentopomap.org'>OpenTopoMap</a> (<a href='https://creativecommons.org/licenses/by-sa/3.0/'>CC-BY-SA</a>)");
	}
	
	/** @since 6.6 */
	public int getMapsTileSize() { 
		return Integer.parseInt(getProperties().getProperty("mapsTileSize", "256"));
	}
	
	/** @since 6.6 */
	public int getMapsZoomOffset() { 
		return Integer.parseInt(getProperties().getProperty("mapsZoomOffset", "0"));
	}
	
	/** @since 6.2 */
	public String getSMTPFromEmail() { 
		return getProperties().getProperty("smtpFromEmail", getSMTPUserID());
	}

	/**
	 * @since 4.7
	 */
	public boolean isSMTPHostTrusted() { 
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"smtpHostTrusted", "false").trim());
	}	

	/**
	 * @since 5.6
	 */	
	public boolean isSMTPStartTLSEnable() {  
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"smtpStartTLSEnable", "false").trim());
	}

	public String getCSVSeparator() {
		return getProperties().getProperty("csvSeparator", ";");
	}
	
	/**
	 * @since 4.2.1 
	 */
	public String getCSVEncoding() { 
		return getProperties().getProperty("csvEncoding", null);
	}

	public String getReportParametersProviderClass() {
		return getProperties().getProperty("reportParametersProviderClass",
				"org.openxava.util.DefaultReportParametersProvider").trim();
	}
	
	/**
	 * Class to create an org.openxava.util.IConnectionRefiner to refine 
	 * the JDBC connections just after get them from the data source and
	 * before use them.
	 * 
	 * @since 5.6
	 */
	public String getConnectionRefinerClass() { 
		return getProperties().getProperty("connectionRefinerClass","").trim();
	}
	
	private String getPersistenceProviderClass() { 
		return getProperties().getProperty("persistenceProviderClass",
				"org.openxava.model.impl.JPAPersistenceProvider").trim();
	}

	/**
	 * @since 5.6 
	 */	
	public String getComponentParsersClasses() { 
		return getProperties().getProperty("componentParsersClasses",
				"org.openxava.component.parse.AnnotatedClassParser").trim();
	}
	/**
	 * @since 5.9 
	 */
	public String getAccessTrackerProvidersClasses() { 
		return getProperties().getProperty("accessTrackerProvidersClasses", "").trim(); 
	}
	

	public String getStyleClass() {
		return getProperties().getProperty("styleClass",
			"org.openxava.web.style.XavaStyle").trim();
	}

	public String getStyleCSS() {
		return getProperties().getProperty("styleCSS", "terra.css").trim(); 
	}
	
	/** @since 6.3 */
	public String getThemes() { 
		return getProperties().getProperty("themes", ""); 
	}	
	
	/**
	 * @since 6.4
	 */
	public boolean isEnterMovesToNextField() { 
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"enterMovesToNextField", "false").trim());
	}

	/**
	 * 
	 * @return true if <code>isMapFacadeAsEJB() == true</code>, otherwise the
	 *         value of <code>mapFacadeAutoCommit</code> property.
	 */
	public boolean isMapFacadeAutoCommit() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"mapFacadeAutoCommit", "false").trim());
	}

	public boolean isDetailOnBottomInCollections() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"detailOnBottomInCollections", "false").trim());
	}

	public boolean isI18nWarnings() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"i18nWarnings", "false").trim());
	}

	public boolean isDuplicateComponentWarnings() {
		if (!duplicateComponentWarningsLoaded) {
			duplicateComponentWarnings = "true"
					.equalsIgnoreCase(getProperties().getProperty(
							"duplicateComponentWarnings", "true").trim());
			duplicateComponentWarningsLoaded = true;
		}
		return duplicateComponentWarnings;
	}

	public boolean isFailOnAnnotationMisuse() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"failOnAnnotationMisuse", "true").trim());
	}

	public int getMaxSizeForTextEditor() {
		if (maxSizeForTextEditor == 0) {
			maxSizeForTextEditor = Integer.parseInt(getProperties()
					.getProperty("maxSizeForTextEditor", "100"));
		}
		return maxSizeForTextEditor;
	}

	public int getPageRowCount() {
		if (pageRowCount == 0) {
			pageRowCount = Integer.parseInt(getProperties().getProperty(
					"pageRowCount", "10"));
		}
		return pageRowCount;
	}
	
	/** @since 7.5 */
	public int getRowActionsPopupThreshold() { 
		if (rowActionsPopupThreshold < 0) {
			rowActionsPopupThreshold = Integer.parseInt(getProperties().getProperty(
				"rowActionsPopupThreshold", "3"));
		}
		return rowActionsPopupThreshold;
	}	
	
	/**
	 * @since 7.4
	 */
	public String getCurrencySymbol() {
		return getProperties().getProperty("currencySymbol");
	}

	/** @since 6.4.2 */
	public int getApplicationPort() { 
		return Integer.parseInt(getProperties().getProperty("applicationPort", "8080"));
	}


	public void setDuplicateComponentWarnings(boolean duplicateComponentWarnings) {
		this.duplicateComponentWarnings = duplicateComponentWarnings;
		duplicateComponentWarningsLoaded = true;
	}

	public Level getJavaLoggingLevel() {
		if (javaLoggingLevel == null) {
			String log = getProperties().getProperty("javaLoggingLevel",
					JAVA_LOGGING_LEVEL_DEFAULT_VALUE).trim();
			try {
				javaLoggingLevel = Level.parse(log);
			} catch (Exception ex) {
				// Because it's a log error, we don't use log, but direct
				// System.err
				javaLoggingLevel = Level
						.parse(JAVA_LOGGING_LEVEL_DEFAULT_VALUE);
				System.err.println("[XavaPreferences.getJavaLoggingLevel] "
						+ XavaResources.getString("incorrect_log_level", log,
								JAVA_LOGGING_LEVEL_DEFAULT_VALUE));
			}
		}
		return javaLoggingLevel;
	}

	public Level getHibernateJavaLoggingLevel() {
		if (hibernateJavaLoggingLevel == null) {
			String log = getProperties().getProperty(
					"hibernateJavaLoggingLevel",
					JAVA_LOGGING_LEVEL_DEFAULT_VALUE).trim();
			try {
				hibernateJavaLoggingLevel = Level.parse(log);
			} catch (Exception ex) {
				// Because it's a log error, we don't use log, but direct
				// System.err
				hibernateJavaLoggingLevel = Level
						.parse(JAVA_LOGGING_LEVEL_DEFAULT_VALUE);
				System.err
						.println("[XavaPreferences.getHibernateJavaLoggingLevel] "
								+ XavaResources.getString(
										"incorrect_log_level", log,
										JAVA_LOGGING_LEVEL_DEFAULT_VALUE));
			}
		}
		return hibernateJavaLoggingLevel;
	}

	/**
	 * If <code>true</code> when an action has no image it uses a button for
	 * display it, else it uses a link.
	 * <p>
	 * 
	 * The default value is <code>false</code>, that is, by default links for
	 * displaying no image actions.<br>
	 */
	public boolean isButtonsForNoImageActions() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"buttonsForNoImageActions", "false").trim());
	}

	/**
	 * If <code>true</code> a upper case conversions will applied to string
	 * arguments for conditions in list and collections.
	 * <p>
	 * 
	 * If <code>true</code> the searching using list or collections are more
	 * flexible (the user can use indistinctly upper or lower case) but can be
	 * slower in some databases (because they cannot use index).
	 * 
	 * The default value is <code>true</code>.<br>
	 */
	public boolean isToUpperForStringArgumentsInConditions() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"toUpperForStringArgumentsInConditions", "true").trim());
	}

	/**
	 * If <code>true</code> filter is show by default for list on init.
	 * <p>
	 * 
	 * The user always have the option to show or hide the filter.<br>
	 * The default value is <code>true</code>.<br>
	 */
	public boolean isShowFilterByDefaultInList() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"showFilterByDefaultInList", "true").trim());
	}
	
	public String getDefaultPersistenceUnit() {
		return getProperties().getProperty("defaultPersistenceUnit", "default");
	}

	public boolean isGenerateDefaultModules() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"generateDefaultModules", "true").trim());
	}

	public int getDefaultLabelFormat() {
		if (defaultLabelFormat >= 0)
			return defaultLabelFormat;
		String labelFormat = getProperties().getProperty("defaultLabelFormat",
				"NORMAL");
		if (labelFormat.equalsIgnoreCase("NORMAL"))
			defaultLabelFormat = MetaPropertyView.NORMAL_LABEL;
		else if (labelFormat.equalsIgnoreCase("SMALL"))
			defaultLabelFormat = MetaPropertyView.SMALL_LABEL;
		else if (labelFormat.equalsIgnoreCase("NO_LABEL"))
			defaultLabelFormat = MetaPropertyView.NO_LABEL;
		else if (labelFormat.equalsIgnoreCase("NOLABEL"))
			defaultLabelFormat = MetaPropertyView.NO_LABEL;
		else {
			defaultLabelFormat = MetaPropertyView.NORMAL_LABEL;
			log.warn(XavaResources.getString(
					"defaultLabelFormat_illegal_value", labelFormat));
		}
		return defaultLabelFormat;
	}

	public String getDefaultLabelStyle() {
		return getProperties().getProperty("defaultLabelStyle", "");
	}

	/** @since 4m5 */
	public String getHelpPrefix() {
		return getProperties().getProperty("helpPrefix", "http://www.openxava.org/OpenXavaDoc/docs/help_").trim(); 
	}

	/** @since 4m5 */
	public String getHelpSuffix() {
		return getProperties().getProperty("helpSuffix", ".html").trim(); 
	}

	/** @since 4m5 */
	public String getDefaultModeController() {
		return getProperties().getProperty("defaultModeController", null);
	}
	
	/** @since 7.2 */
	public boolean isTurnOffWebSecurity() { 
		return "true".equalsIgnoreCase(getProperties().getProperty("turnOffWebSecurity", "false").trim());
	}
	
	/** @since 7.2 */
	public boolean isUnsafeEvalInScripts() { 
		return "true".equalsIgnoreCase(getProperties().getProperty("unsafeEvalInScripts", "false").trim());
	}
	
	
	
	/**
	 * 
	 * @since 5.4
	 */
	public boolean isUseIconsInsteadOfImages() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"useIconsInsteadOfImages", "true").trim());
	}
	

	/** @since 4m5 */
	public boolean isHelpInNewWindow() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"helpInNewWindow", "true").trim());
	}
	
	
	/** @since 5.6 */
	public boolean isHelpAvailable() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"helpAvailable", "false").trim()); 
	}
	
	/** @since 5.7 */
	public boolean isFlowLayout() {  
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"flowLayout", "false").trim());
	}
	
	/** @since 4m5 */
	public boolean isCustomizeList(){
		return "true".equalsIgnoreCase(getProperties().getProperty("customizeList", "true").trim());		
	}
	
	/** @since 4m5 */
	public boolean isResizeColumns(){
		return "true".equalsIgnoreCase(getProperties().getProperty("resizeColumns", "false").trim());
	}
	
	
	/** @since 4m6 */
	public boolean isSaveAndStayForCollections(){
		return "true".equalsIgnoreCase(getProperties().getProperty("saveAndStayForCollections", "true").trim());
	}
	
	/** @since 4m6 */
	public boolean isShowLabelsForToolBarActions(){
		return "true".equalsIgnoreCase(getProperties().getProperty("showLabelsForToolBarActions", "true").trim());
	}
	
	/** @since 4m6
	 * 
	 * If <code>true</code> it ignore accents to string arguments for conditions in list and collections. <p>
	 * 
	 * The default value is <code>false</code>.<br>
	 */
	public boolean isIgnoreAccentsForStringArgumentsInConditions() {  
		return "true".equalsIgnoreCase(getProperties().getProperty("ignoreAccentsForStringArgumentsInConditions", "false" ).trim()); 
	}
	
	/** @since 4.3 */
	public boolean isSummationInList() {
		return "true".equalsIgnoreCase(getProperties().getProperty(
				"summationInList", "true").trim());
	}
	
	/** @since 4.6 */
	public boolean isShowIconForViewReadOnly() {
		return "true".equalsIgnoreCase(getProperties().getProperty("showIconForViewReadOnly", "true").trim());
	}
	
	/** @since 4.7.1 */
	public boolean isAlignedByColumns() { 
		return "true".equalsIgnoreCase(getProperties().getProperty("alignedByColumns", "false").trim());
	}
	
	/** @since 4.8 */
	public boolean isFilterOnChange() { 
		return "true".equalsIgnoreCase(getProperties().getProperty("filterOnChange", "true").trim());
	}
	
	/** @since 5.0 */
	public String getFilePersistorClass() {
		return getProperties().getProperty("filePersistorClass","org.openxava.web.editors.FileSystemPersistor").trim();
	}
	
	/** @since 5.0 */
	public String getFilesPath() { 
		String path = getProperties().getProperty("filesPath");
		return path != null ? path : System.getProperty("user.home") + System.getProperty("file.separator") + "oxfiles";
	}
	
	/** @since 7.1 */
	public String[] getDefaultDescriptionPropertiesValueForDescriptionsList() { 
		String properties = getProperties().getProperty("defaultDescriptionPropertiesValueForDescriptionsList", "nombre,name,title,titulo,descripcion,description"); 
		return properties.split(",");
	}
	
	/** @since 7.1.2 */
	public String getTrustedHostsForImages() {  
		return getProperties().getProperty("trustedHostsForImages", "");
	}
	
	/** @since 7.1.2 */
	public String getTrustedHostsForScripts() {  
		return getProperties().getProperty("trustedHostsForScripts", "");
	}
	
	/** @since 7.1.2 */
	public String getTrustedHostsForStyles() {  
		return getProperties().getProperty("trustedHostsForStyles", "");
	}
	
	/** @since 7.1.2 */
	public String getTrustedHostsForFrames() {  
		return getProperties().getProperty("trustedHostsForFrames", "");
	}
	
	/** @since 7.5 */
	public String getEntityManagerFactoryProviderClass() {
		return getProperties().getProperty("entityManagerFactoryProviderClass", 
				"org.openxava.jpa.impl.EntityManagerFactoryProvider").trim();
	}
	
}
