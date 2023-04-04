package org.openxava.web.dwr;

import java.time.*;

import javax.servlet.http.*;

import org.openxava.tab.Tab;
import org.openxava.util.*;

public class Calendar extends DWRBase{

	transient private HttpServletRequest request; 
	transient private HttpServletResponse response; 
	private String application;
	private String module;
	
	public void getEvents(HttpServletRequest request, 
			HttpServletResponse response, 
			String application, 
			String module) {
			initRequest(request, response, application, module); 
			
			//Tab tab = getTab(request, application, module, tabObject); 
			
			this.application = application;
			this.module = module;
			System.out.println("getEvents");
			Messages errorss = new Messages();
			errorss.add(null);
			
			//CalendarEventIterator ce = new CalendarEventIterator(getTab(), getView(), request, errorss);
			try {
				String action = "hola2";
				LocalDate date = LocalDate.parse("2023-03-30");
				//ce.getEvents(action, date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		
	}
	 
	public void test(HttpServletRequest request, 
			HttpServletResponse response, 
			String application, 
			String module) {
		this.application = application;
		this.module = module;
		System.out.println(" Aloha: " + application);
		System.out.println(" Aloha: " + module);
		Tab tab = getTab(request, application, module);
		System.out.println(tab.getTotalSize());
	}
	
	
	 
		private static Tab getTab(HttpServletRequest request, String application, String module) {
			Tab tab = (org.openxava.tab.Tab)		
			  getContext(request).get(application, module, "xava_tab");
			request.setAttribute("xava.application", application);
			request.setAttribute("xava.module", module);
			tab.setRequest(request);
			return tab;
		}

	
}
