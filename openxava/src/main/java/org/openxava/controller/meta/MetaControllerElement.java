package org.openxava.controller.meta;

import org.openxava.util.*;
import org.openxava.util.meta.*;

/**
 * Controller' element: it can be an action or a subcontroller.
 * <br>
 * Create on 2 sept. 2016 (11:31:50)
 * @author Ana Andres
 */
public abstract class MetaControllerElement extends MetaElement{

	private String image;
	private String icon;
	private String mode;
	
	public boolean appliesToMode(String mode) {
		if ("NONE".equals(getMode())) return false;
		return Is.emptyString(getMode()) || getMode().equals(mode);
	}
	
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		if (image != null && image.startsWith("images/")) this.image = image.substring(7); 
		else this.image = image;
	}
	
	public String getIcon() {
		return icon;
	}
	
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	public String getMode() {
		return mode;
	}
	
	public void setMode(String mode) {
		this.mode = mode;
	}

	public abstract String getId();
	
	public boolean hasImage() {
		return !Is.emptyString(this.image);
	}
	
	public boolean hasIcon() { 
		return !Is.emptyString(this.icon);
	}
}
