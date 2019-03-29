package org.openxava.formatters;

import javax.servlet.http.*;

import org.openxava.util.*;
import org.openxava.web.editors.*;
import org.openxava.web.style.*;

/**
 * For stereotypes FILE and ARCHIVO in list mode. <p>
 * 
 * @author Jeromy Altuna
 */
public class FileListFormatter implements IFormatter {

	public String format(HttpServletRequest request, Object object) throws Exception {
		if(Is.empty(object)) return "";
		AttachedFile file = FilePersistorFactory.getInstance().find((String) object);
		if(file != null) {
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
		return "";
	}

	public Object parse(HttpServletRequest request, String string) throws Exception {		
		return null;
	}

}
