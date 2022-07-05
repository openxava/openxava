package org.openxava.session;

import java.io.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.*;
import org.openxava.annotations.*;
import org.openxava.jpa.*;

/**
 * Used by Gallery. <p>
 * 
 * Until v6.x it was persisted using Hibernate, since v7.0 it uses JPA.<br>
 * 
 * @author Javier Paniza
 */

@Entity 
@Table(name = "IMAGES") 
public class GalleryImage implements Serializable {
	
	@Id @GeneratedValue(generator="system-uuid") @Hidden 
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	@Column(name="ID", length=32)
	private String oid;
	
	@Column(name="GALLERY", length=32)
	private String galleryOid;
	
	@Column(length=16777216)
	private byte [] image;
		
	public static GalleryImage find(String oid) { 
		return XPersistence.getManager().find(GalleryImage.class, oid);
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
