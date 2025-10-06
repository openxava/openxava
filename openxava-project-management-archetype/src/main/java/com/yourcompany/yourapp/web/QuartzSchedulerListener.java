package com.yourcompany.yourapp.web;

import javax.servlet.*;
import javax.servlet.annotation.*;

import org.apache.commons.logging.*;
import org.openxava.util.*;
import org.quartz.impl.*;

@WebListener
public class QuartzSchedulerListener implements ServletContextListener {
	
	private static final Log log = LogFactory.getLog(QuartzSchedulerListener.class);

    public void contextInitialized(ServletContextEvent sce) {
        try {
            StdSchedulerFactory.getDefaultScheduler().start();
        } 
        catch (Exception ex) {
        	log.error(XavaResources.getString("quartz_scheduler_start_error"), ex);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        try {
        	StdSchedulerFactory.getDefaultScheduler().shutdown();
        } 
        catch (Exception ex) {
        	log.error(XavaResources.getString("quartz_scheduler_stop_error"), ex);
        }
    }
    
}
