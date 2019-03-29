package org.openxava.util;

import java.util.prefs.*;

import javax.servlet.http.*;

import org.apache.commons.logging.*;
import org.openxava.util.impl.*;

/**
 * Utilities to work with users. <p>
 * 
 * @author Javier Paniza
 */

public class Users {
	private static Log log = LogFactory.getLog(Users.class);
	final private static ThreadLocal current = new ThreadLocal(); 
	final private static ThreadLocal currentUserInfo = new ThreadLocal();
	final private static ThreadLocal<String> currentIP = new ThreadLocal<>(); 
	public final static String ADMIN_OX = "__ADMINOX__";
	public final static String SHARED_OX = "__SHAREDOX__"; 
	
	/**
	 * The user name associated to the current thread. <p>
	 * 
	 * @return <code>null</code> if no user logged.
	 */
	public static String getCurrent() {
		return (String) current.get();
	}
	
	/**
	 * 
	 * @since 6.1
	 */
	public static String getCurrentIP() { 
		return (String) currentIP.get();
	}
	
	/**
	 * Preferences of the user associated to the current thread. <p>
	 * 
	 * You can use this preference for read and update user preferences,
	 * in this way:
	 * <pre>
	 * // Obtain preferences for the current user and node (an arbitrary category of your choice).
	 * Preferences preferences = Users.getCurrentPreferences().node("mynode");
	 * 
	 * // Read a property value
	 * boolean rowsHidden = preferences.getBoolean(ROWS_HIDDEN, false);
	 *  
	 * // Modify and save a property  
	 * preferences.putBoolean(ROWS_HIDDEN, rowsHidden);
	 * preferences.flush();
	 * </pre>
	 * Remember, you must call flush() always in order to make persistent the changes in 
	 * preferences.<br>
	 * 
	 * @return  The preferences object associated to the current user.
	 * @throws BackingStoreException  Some problem on load preferences.
	 */
	public static Preferences getCurrentPreferences() throws BackingStoreException {
		String organization = getCurrentUserInfo().getOrganization();
		String key = Is.emptyString(organization)?getCurrent():organization + "__" + getCurrent();
		return UserPreferences.getForUser(key);
	}
	
	/**
	 * Preferences of the admin user. <p>
	 * 
	 * @return  The preferences object associated to the admin user.
	 * @throws BackingStoreException  Some problem on load preferences.
	 */
	public static Preferences getAdminPreferences() throws BackingStoreException { 
		return UserPreferences.getForUser(ADMIN_OX);
	}
	
	/**
	 * Preferences of the shared user. <p>
	 * 
	 * @return  The preferences object associated to the shared user.
	 * @throws BackingStoreException  Some problem on load preferences.
	 */
	public static Preferences getSharedPreferences() throws BackingStoreException {
		return UserPreferences.getForUser(SHARED_OX);
	}
	
	/**
	 * Info about the current logged user. <p>
	 * 
	 * It only returns meaningful info if we are inside a portal.<br>
	 * 
	 * @return Not null. If not info available it returns a UserInfo object with no info.
	 */
	public static UserInfo getCurrentUserInfo() {
		UserInfo userInfo = (UserInfo) currentUserInfo.get();
		if (userInfo == null) userInfo = new UserInfo();
		userInfo.setId(getCurrent());
		return userInfo;
	}	
	
	/**
	 * Associated an user to the current thread. <p>
	 */	
	public static void setCurrent(String userName) {
		current.set(userName);
		currentUserInfo.set(null); 
	}
	
	/**
	 * Associated the UserInfo object to the current thread. <p>
	 */	
	public static void setCurrentUserInfo(UserInfo userInfo) { 
		current.set(userInfo.getId());
		currentUserInfo.set(userInfo); 
	}
	
	
	/**
	 * Associated the user of the request to the current thread. <p>
	 * 
	 * Takes into account JetSpeed 1.5 user managament, althought
	 * it's generic enought to work in any servlet container.
	 */
	public static void setCurrent(HttpServletRequest request) {		
        Object rundata = request.getAttribute("rundata");
        String portalUser = (String) request.getSession().getAttribute("xava.portal.user"); 
        String webUser = (String) request.getSession().getAttribute("xava.user"); 
        String user = portalUser == null?webUser:portalUser;
        if (Is.emptyString(user) && rundata != null) {
			PropertiesManager pmRundata = new PropertiesManager(rundata);
			try {
				// Using introspection for not link OpenXava to turbine and jetspeed1.x
				// This is temporal. In future JSR-168 compatible, and remove this code 
				Object jetspeedUser = pmRundata.executeGet("user");
				PropertiesManager pmUser = new PropertiesManager(jetspeedUser);
				user = (String) pmUser.executeGet("userName");
			}
			catch (Exception ex) {				
				log.warn(XavaResources.getString("warning_get_user"),ex);
				user = null;
			}			
		}		
		current.set(user);
		request.getSession().setAttribute("xava.user", user); 
				
		currentUserInfo.set(request.getSession().getAttribute("xava.portal.userinfo")); 
		
		currentIP.set(request.getRemoteAddr());
	}
	

} 
