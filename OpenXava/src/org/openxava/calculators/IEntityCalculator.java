package org.openxava.calculators;

import java.rmi.*;


/**
 * @deprecated Since OpenXava 2.0. Use IModelCalculator instead     
 * @author Javier Paniza
 */
public interface IEntityCalculator extends ICalculator {
	
	void setEntity(Object entity) throws RemoteException;

}
