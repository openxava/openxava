package org.openxava.test.actions;

import java.net.*;
import java.util.*;
import java.util.zip.*;

import org.openxava.actions.*;
import org.openxava.util.*;


/**
 * 
 * @author Javier Paniza
 */

public class TranslateAllCarrierNameAction extends ViewBaseAction {

	public void execute() throws Exception {
		/* tmr
		for (Iterator it=Carrier.findAll().iterator(); it.hasNext(); ) {
			Carrier c = (Carrier) it.next();
			c.translate();
		}
		getView().refreshCollections();
		*/
		
		
		System.out.println("[TranslateAllCarrierNameAction.execute] B"); // tmr
		long ini = System.currentTimeMillis(); // tmr
		// TMR LO DE ABAJO YA VA. FALTA:
		// TMR   - QUITAR PREFIJO META-INF/resources EN EL RESULTADO
		// TMR   - MOVERLO A UNA CLASE DE UTILIDAD
		// TMR   - CACHE: COMPROBAR RENDIMIENTO
		Enumeration<URL> e = getClass().getClassLoader().getResources("META-INF/resources/xava/editors/style");
		while (e.hasMoreElements()) {
			URL url = e.nextElement();
			if (url.getProtocol().equals("jar")) {
				String jarURL = url.getFile().replace("file:", "");
				jarURL = Strings.noLastTokenWithoutLastDelim(jarURL, "!");
				System.out.println("[TranslateAllCarrierNameAction.execute] jarURL=" + jarURL); // tmr
				ZipFile zip = new ZipFile(jarURL);
				Enumeration<? extends ZipEntry> entries = zip.entries();
				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					if (entry.getName().startsWith("META-INF/resources/xava/editors/style/") &&	entry.getName().endsWith(".css")) {
						System.out.println("[TranslateAllCarrierNameAction.execute] " + entry.getName()); // tmr
					}
				}

			}
		}

		long cuesta = System.currentTimeMillis() - ini; // tmr
		System.out.println("[TranslateAllCarrierNameAction.execute] cuesta=" + cuesta); // tmr
	}

}
