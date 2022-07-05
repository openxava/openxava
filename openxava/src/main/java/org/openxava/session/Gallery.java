package org.openxava.session;

import java.util.*;

import javax.persistence.*;

import org.openxava.jpa.*;


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
		Query query = XPersistence.getManager().createQuery("select oid from GalleryImage where galleryOid=:galleryOid");
		query.setParameter("galleryOid", oid);
		return query.getResultList();
	}

	public void addImage(byte [] image) {
		GalleryImage galleryImage = new GalleryImage();
		galleryImage.setGalleryOid(oid);
		galleryImage.setImage(image);
		XPersistence.getManager().persist(galleryImage);  
	}

	public void removeImage(String oid) {
		GalleryImage image = (GalleryImage) XPersistence.getManager().find(GalleryImage.class, oid);
		XPersistence.getManager().remove(image);
	}
	
}
