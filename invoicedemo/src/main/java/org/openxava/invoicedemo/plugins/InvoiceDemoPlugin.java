package org.openxava.invoicedemo.plugins; // tmr Borrar paquete al acabar

import java.net.*;

import org.hotswap.agent.annotation.*;

// tmr Quitar

@Plugin(name = "InvoiceDemo", testedVersions = { "7.5+" }) // tmr ¿Este nombre?
public class InvoiceDemoPlugin {
	
    
	@OnResourceFileEvent(path = "/", filter = ".*.xml", events = {FileEvent.MODIFY})
    public void onResourceChange(URL url) {
		// TMR ME QUEDÉ POR AQUÍ: NO FUNCIONA
    	System.out.println("[InvoiceDemoPlugin.onResourceChange] url=" + url); // tmr
    }
    
        
}