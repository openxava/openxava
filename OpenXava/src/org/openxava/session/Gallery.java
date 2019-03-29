package org.openxava.session;

import java.util.*;



import org.hibernate.*;
import org.openxava.hibernate.*;

/**
 * Manages an images gallery. <p>
 * 
 * It's an OpenXava session object, and it's used to implement the stereotype
 * IMAGES_GALLERY/GALERIA_IMAGENES.<br>
 * 
 * @author Javier Paniza
 */
public class Gallery {
	
	private Map images = null;
	private String oid;
	private boolean maximized = false;
	private String maximizedOid;
	private String title;
	private boolean readOnly = false;

	
	
	public void loadAllImages() {
		if (images != null) images.clear();
		Query query = XHibernate.getSession().createQuery("from GalleryImage where galleryOid=:galleryOid");
		query.setString("galleryOid", oid);		
		for (Iterator it = query.list().iterator(); it.hasNext(); ) {
			addImage((GalleryImage) it.next());
		}
	}
	
	public void addImage(byte [] image) {
		GalleryImage galleryImage = new GalleryImage();
		galleryImage.setGalleryOid(oid);
		galleryImage.setImage(image);
		XHibernate.getSession().save(galleryImage);		
		addImage(galleryImage);
	}
	
	private void addImage(GalleryImage image) {
		if (images == null) images = new HashMap();
		images.put(image.getOid(), image);
	}
	
	public void removeImage(String oid) {
		GalleryImage image = (GalleryImage) XHibernate.getSession().get(GalleryImage.class, oid);
		XHibernate.getSession().delete(image);
		images.remove(oid);
	}

	public Collection getImages() {
		return images == null?Collections.EMPTY_LIST:images.values();
	}
	
	public byte [] getImage(String oid) {
		if (images == null) return null;
		GalleryImage image = (GalleryImage) images.get(oid);
		if (image == null) return null;
		return image.getImage();
	}

	public boolean isMaximized() {
		return maximized;
	}

	public void setMaximized(boolean maximized) {
		this.maximized = maximized;
	}

	public String getMaximizedOid() {
		return maximizedOid;
	}

	public void setMaximizedOid(String maximizedOid) {
		this.maximizedOid = maximizedOid;
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public boolean isEmpty() {
		if (images == null) return true;
		return images.isEmpty();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}
	
}
