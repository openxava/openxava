package org.openxava.calculators;

import java.rmi.*;


/**
 * Calculator that receives a model object (entity or aggregate) that is used as source to execute the calculation. <p>
 * 
 * This type of calculator is used to generate calculated properties code
 * on generate <i>EntityBeans</i> or <i>POJO</i> model class and to calculate
 * the property values in the tab. Also it can be used for calculate in user interface. <p>
 * 
 * <b>Only for EJB2 (classic) version:</b><br>
 * Within a <i>EntityBean</i> this calculator receives the bean itself, while
 * in the tab it recevies the remote object. So if we want that this calculator
 * can be used within <i>EntityBean</i> and in the tab the better way is used one 
 * interface that is implemented by remote interface and bean class, and use this
 * interface on cast within calculator. The OpenXava EJB code generator creates
 * a interfaz that is implemented by both, and it can be used for this. <p>
 *     
 * @author Javier Paniza
 */
public interface IModelCalculator extends ICalculator {
	
	/**
	 * The model object that contains the member that uses the calculator. <p>
	 */
	void setModel(Object model) throws RemoteException;

}
