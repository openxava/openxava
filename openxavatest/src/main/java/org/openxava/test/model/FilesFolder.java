package org.openxava.test.model;

import java.util.*;
import javax.persistence.*;
import org.openxava.annotations.*;

/**
 * 
 * @author Javier Paniza 
 */

@Entity
@Table(name="FOLDER")
public class FilesFolder extends Nameable {

    @ManyToOne @DescriptionsList
    private FilesFolder parent; // parent, not filesFolder, to test a case

    @OneToMany(cascade=CascadeType.ALL, mappedBy="parent")
    private Collection<FilesFolder> subfolders;

    // Without @CollectionView to test a case
    @OneToMany(mappedBy="folder")
    private Collection<File> files;
	
	@PreRemove
	private void detachFiles() {
		for (File file: files) {
			file.setFolder(null);
		}
	}

	public FilesFolder getParent() {
		return parent;
	}

	public void setParent(FilesFolder parent) {
		this.parent = parent;
	}

	public Collection<FilesFolder> getSubfolders() {
		return subfolders;
	}

	public void setSubfolders(Collection<FilesFolder> subfolders) {
		this.subfolders = subfolders;
	}

	public Collection<File> getFiles() {
		return files;
	}

	public void setFiles(Collection<File> files) {
		this.files = files;
	}    

}