package com.tuempresa.tuaplicacion.web;

import javax.servlet.*;
import javax.servlet.annotation.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.quartz.impl.*;

@WebListener
public class PlanificadorQuartzListener implements ServletContextListener {
	
	private static final Log log = LogFactory.getLog(PlanificadorQuartzListener.class);

    public void contextInitialized(ServletContextEvent sce) {
        try {
            StdSchedulerFactory.getDefaultScheduler().start();
        } 
        catch (Exception ex) {
        	log.error(XavaResources.getString("error_arrancar_planificador_quartz"), ex);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        try {
        	StdSchedulerFactory.getDefaultScheduler().shutdown();
        } 
        catch (Exception ex) {
        	log.error(XavaResources.getString("error_parar_planificador_quartz"), ex);
        }
    }
    
}
