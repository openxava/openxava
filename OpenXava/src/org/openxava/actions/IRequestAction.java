package org.openxava.actions;

import javax.servlet.http.*;

/**
 * Action that receive a servlet request. <p>
 * 
 * It is implemented by {@link BaseAction}, so if your 
 * extends from it you can access to the request 
 * just by calling to {@link BaseAction#getRequest} (since 4m1). <p>
 * 
 * With this action you can access directly to the
 * web application resources (by means request), but
 * it ties to implementation technology (servlets),
 * hence it's better to elude it if you have another
 * option and you're thinking in migrate to another tecnology. <p>
 * 
 * But it's needed form some issues. As this action type is
 * used for specific task, it's possible refactoring and create
 * more specific (in functional terms) and abstracts (in tecnologic terms)
 * actions that it's not link to servlets tecnology.<br>
 *   
 * @author Javier Paniza
 */

public interface IRequestAction {
	
	void setRequest(HttpServletRequest request);

}
