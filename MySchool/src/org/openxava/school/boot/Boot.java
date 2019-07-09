package org.openxava.school.boot;

/* tmp
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.webapp.*;
*/

/*
 * tmp Falta:
 * - Minimizar la cantidad de jar. ¿Dejar de usar Jetty all?
 * - Nombre y lugar para esta clase.
 * - ¿Esta clase se puede incluir en OpenXava?
 * - Evitar el "Mensaje select a way to run MySchool" al ejecutar la aplicación
 * - Pensar en como seleccionar la clase por defecto al ejecutar la aplicación
 */

public class Boot {

    public static void main(String[] args) throws Exception {
    	/*
    	 
        // 1. Creating the server on port 8080
        Server server = new Server(8080);
 
        // 2. Creating the WebAppContext for the created content
        WebAppContext ctx = new WebAppContext();
        ctx.setResourceBase("web");
        ctx.setContextPath("/MySchool"); // tmp ¡Ojo! A piñon fijo
         
        //3. Including the JSTL jars for the webapp.
         */
        //ctx.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",".*/[^/]*jstl.*\\.jar$");
        /*
     
        //4. Enabling the Annotation based configuration
        org.eclipse.jetty.webapp.Configuration.ClassList classlist = org.eclipse.jetty.webapp.Configuration.ClassList.setServerDefault(server);
        classlist.addAfter("org.eclipse.jetty.webapp.FragmentConfiguration", "org.eclipse.jetty.plus.webapp.EnvConfiguration", "org.eclipse.jetty.plus.webapp.PlusConfiguration");
        classlist.addBefore("org.eclipse.jetty.webapp.JettyWebXmlConfiguration", "org.eclipse.jetty.annotations.AnnotationConfiguration");
         
        //5. Setting the handler and starting the Server
        server.setHandler(ctx);
        server.start();
        server.join();
        */
 
    }

	
}
