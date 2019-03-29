package org.openxava.actions;

import java.util.*;

import javax.inject.*;



import org.apache.commons.logging.*;
import org.openxava.calculators.*;
import org.openxava.model.*;
import org.openxava.model.meta.*;
import org.openxava.session.*;
import org.openxava.util.*;
import org.openxava.view.*;

/**
 * 
 * @author Javier Paniza
 */

public class EditGalleryAction extends ViewBaseAction implements INavigationAction { 
		
	private static Log log = LogFactory.getLog(EditGalleryAction.class);
	
	private String galleryProperty;
	private String viewObject; 
	private View containerView; 
	private boolean editable; 

	@Inject
	private Gallery gallery;	
	
	
	
	public void execute() throws Exception {
		editable = getContainerView().isEditable(galleryProperty); 
		String oid = getContainerView().getValueString(galleryProperty);
		if (Is.emptyString(oid)) {
			UUIDCalculator cal = new UUIDCalculator();  
			oid = (String) cal.calculate();
			getContainerView().setValue(galleryProperty, oid);
			if (!getContainerView().isKeyEditable()) { // Modifying
				updateOidInObject(oid);
			}
		}
		gallery.setOid(oid);
		gallery.loadAllImages();		
		gallery.setTitle(XavaResources.getString("gallery_title", 
				Labels.get(galleryProperty, Locales.getCurrent()), 
				Labels.get(getContainerView().getModelName(), Locales.getCurrent()), 
				getObjectDescription()));				
		if (gallery.isEmpty()) {
			addMessage("no_images");
		}
		gallery.setReadOnly(!isEditable());
		showDialog();
	}

	private void updateOidInObject(String oid) throws Exception {
		Map values = new HashMap();
		values.put(galleryProperty, oid);
		MapFacade.setValuesNotTracking(getContainerView().getModelName(), getContainerView().getKeyValues(), values); 
	}

	private String getObjectDescription() {
		try {
			StringBuffer result = new StringBuffer();
			for (Iterator it=getContainerView().getMetaModel().getMetaPropertiesKey().iterator(); it.hasNext();) {
				MetaProperty p = (MetaProperty) it.next();
				if (!p.isHidden()) {
					Object value = getContainerView().getValue(p.getName());
					if (value != null) {
						if (result.length() > 0) result.append('/');
						result.append(value);
					}
				}
			}

			String [] descriptionProperties = { "name", "nombre", "title", "titulo", "description", "descripcion" }; 
			for (int i = 0; i < descriptionProperties.length; i++) {
				String des = (String) getContainerView().getValue(descriptionProperties[i]);
				if (!Is.emptyString(des)) {
					if (isHtml(des)) continue; 
					if (result.length() > 0) result.append(" - ");
					result.append(des);
					break;
				}				
			}
			
			return result.toString();
		}
		catch (Exception ex) { 
			log.error("[EditGalleryAction.getObjectDescription] " + XavaResources.getString("object_description_warning"),ex);  
			return XavaResources.getString("object_description_warning");
		}		
	}
	
	private boolean isHtml(String des) { 
		return des.contains("<") && des.contains(">"); 
	}

	private View getContainerView() { 
		if (containerView == null) {
			containerView = (View) getContext().get(getRequest(), viewObject==null?"xava_view":viewObject);
		}
		return containerView;		
	}

	private boolean isEditable() throws XavaException {
		return editable; 
	}

	public String getGalleryProperty() {
		return galleryProperty;
	}

	public void setGalleryProperty(String galleryOID) {
		this.galleryProperty = galleryOID;
	}

	public String[] getNextControllers() throws Exception {
		return isEditable()?new String [] { "Gallery" }:new String [] { "Close" };
	}

	public String getCustomView() throws Exception {
		return "xava/editors/gallery";
	}

	public Gallery getGallery() {
		return gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}

	public String getViewObject() {
		return viewObject;
	}

	public void setViewObject(String viewObject) {
		this.viewObject = viewObject;
	}

}
