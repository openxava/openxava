package org.openxava.session;

import java.io.*;

import org.openxava.hibernate.*;





/**
 * Used by Gallery. <p>
 * 
 * It's persistent, using Hibernate.<br>
 * 
 * @author Javier Paniza
 */

public class GalleryImage implements Serializable {
	
	private String oid;
	private String galleryOid;
	private byte [] image;
	
	public static GalleryImage find(String oid) { 
		return XHibernate.getSession().find(GalleryImage.class, oid);
	}
	
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getGalleryOid() {
		return galleryOid;
	}
	public void setGalleryOid(String galleryOid) {
		this.galleryOid = galleryOid;
	}
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
	
}
