package org.openxava.web.editors;

import javax.persistence.*;

import org.openxava.model.*;

/**
 * Class that allows to implement stereotypes FILE and FILES. <p>
 * 
 * It's a JPA entity. <p>
 * 	
 * @author Jeromy Altuna
 */
@Entity
@Table(name="OXFILES", indexes = {@Index(columnList = "libraryId")}) 
public class AttachedFile extends Identifiable {
	
	private String name;	
	private byte[] data;
	
	@Column(length=32) 
	private String libraryId;
	
	/** 
	 * @return the file name 
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/** 
	 * @return a array containing the file data
	 */
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	
	/**
	 * Files can be organized to form a set of related files (fileset, directory, 
	 * folder, etc), here called library. 
	 * 
	 * <p>The value returned by this method can be <tt>null<tt> in the case of 
	 * files that do not belong to any fileset. 
	 * 
	 * @return Unique identifier of the fileset
	 */
	public String getLibraryId() {
		return libraryId;
	}
	public void setLibraryId(String libraryId) {
		this.libraryId = libraryId;
	}	
}
