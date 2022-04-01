package org.openxava.util;

import java.rmi.*;
import java.util.*;
import org.apache.commons.logging.*;
import org.openxava.actions.*;
import org.openxava.calculators.*;
import org.openxava.component.*;
import org.openxava.ejbx.*;
import org.openxava.util.impl.*;


/**
 * Executes code in server. <p>
 * 
 * Connects to EJB server and executes the code using
 * a SessionBean.
 * 
 * @author Javier Paniza
 */
public class Server {
	
	private static Log log = LogFactory.getLog(Server.class);
	
	private static Map remotes;
	
	public static Object calculate(ICalculator calculator, String packageName) throws Exception {
		try {
			return getRemote(packageName).calculate(calculator);
		}
		catch (RemoteException ex) {
			annulRemote(packageName);
			return getRemote(packageName).calculate(calculator);
		}		
	}
	
	public static Object calculateWithoutTransaction(ICalculator calculator, String packageName) throws Exception {
		try {
			return getRemote(packageName).calculateWithoutTransaction(calculator);
		}
		catch (RemoteException ex) {
			annulRemote(packageName);
			return getRemote(packageName).calculateWithoutTransaction(calculator);
		}		
	}
	
	public static IRemoteAction execute(IRemoteAction action, String packageName) throws Exception {
		try {
			return getRemote(packageName).execute(action);
		}
		catch (RemoteException ex) {
			annulRemote(packageName);
			return getRemote(packageName).execute(action);
		}		
	}
			
	private static ServerRemote getRemote(String packageName) throws RemoteException {
		try {						
			packageName = Strings.change(packageName, ".", "/");
			ServerRemote remote = (ServerRemote) getRemotes().get(packageName);
			if (remote == null) {							
				Object ohome = null;
				try {					
					ohome = BeansContext.get().lookup("ejb/"+packageName+"/Server");
				}
				catch (Exception ex) {					
					packageName = MetaComponent.getQualifiedPackageForUnqualifiedPackage(packageName);
					ohome = BeansContext.get().lookup("ejb/"+packageName+"/Server"); 
				}				
				ServerHome home = (ServerHome) ohome; 
				remote = home.create();
				getRemotes().put(packageName, remote);				
			}		
			return remote;
		}
		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RemoteException(XavaResources.getString("server_remote_exception"));
		}		
	}

	
	private static Map getRemotes() {
		if (remotes == null) {
			remotes = new HashMap();
		}
		return remotes;
	}
	
	private static void annulRemote(String packageName) {
		packageName = Strings.change(packageName, ".", "/");
		getRemotes().remove(packageName);			
	}	
		
}
