package org.openxava.session;

import java.util.*;
import org.openxava.hibernate.*;

/**
 * Manages an images gallery. <p>
 * 
 * It's used to implement the stereotype IMAGES_GALLERY/GALERIA_IMAGENES.<br>
 * It is in session package for historical reasons, it was a session object in its first incarnation.
 * 
 * @author Javier Paniza
 */
public class Gallery {
	
	private String oid;
	
	private Gallery() {
	}
	
	public static Gallery find(String oid) {
		Gallery gallery = new Gallery();
		gallery.oid = oid;
		return gallery;
	}
	
	public Collection<String> getImagesOids() {
		org.hibernate.query.Query<String> query = XHibernate.getSession().createQuery("select oid from GalleryImage where galleryOid=:galleryOid");
		query.setParameter("galleryOid", oid);
		return query.list();
	}

	public void addImage(byte [] image) {
		GalleryImage galleryImage = new GalleryImage();
		galleryImage.setGalleryOid(oid);
		galleryImage.setImage(image);
		XHibernate.getSession().save(galleryImage);		
	}

	public void removeImage(String oid) {
		GalleryImage image = (GalleryImage) XHibernate.getSession().get(GalleryImage.class, oid);
		XHibernate.getSession().delete(image);
	}
	
}
