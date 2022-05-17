package org.openxava.formatters;

import javax.servlet.http.*;

import org.openxava.util.*;
import org.openxava.web.editors.*;
import org.openxava.web.style.*;

/**
 * For FILE/ARCHIVO stereotype and @File annotation in list mode. <p>
 * 
 * @author Jeromy Altuna
 * @author Javier Paniza
 */
public class FileListFormatter implements IFormatter {

	public String format(HttpServletRequest request, Object object) throws Exception {
		if(Is.empty(object)) return "";
		AttachedFile file = FilePersistorFactory.getInstance().find((String) object);
		if(file != null) {
			return Files.isImage(file.getName())?toImageURL(request, file):toFileURL(request, file);			
		}
		return "";
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {		
		return null;
	}
	
	private String toFileURL(HttpServletRequest request, AttachedFile file) { 
		return new StringBuilder("<a href='").append(request.getContextPath())
			.append("/xava/xfile?application=")
			.append(request.getParameter("application"))
			.append("&module=").append(request.getParameter("module"))
			.append("&fileId=").append(file.getId())
		    .append("&dif=") .append(System.currentTimeMillis())
		    .append("'target='_blank'>")
		    .append("<span class=\"")
		    .append(Style.getInstance().getAttachedFile())
		    .append("\">").append(file.getName()).append("</span>")
		    .append("</a>") 
		    .toString();		
	}
	
	private String toImageURL(HttpServletRequest request, AttachedFile file) { 
		return new StringBuilder("<img src='").append(request.getContextPath())
			.append("/xava/xfile?application=")
			.append(request.getParameter("application"))
			.append("&module=").append(request.getParameter("module"))
			.append("&fileId=").append(file.getId())
		    .append("&dif=") .append(System.currentTimeMillis())
		    .append("'/>")
		    .toString();	
	}

}
