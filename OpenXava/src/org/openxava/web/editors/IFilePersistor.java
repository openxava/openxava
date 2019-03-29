package org.openxava.web.editors;

import java.util.*;

/**
 * Interface used to interact with a file container (database, file system, etc) <p>
 *  
 * @author Jeromy Altuna
 */
public interface IFilePersistor {
	
	/**
	 * Persist a File (stored somewhere). <p>  
	 * This method should generate a unique identifier for the file. <p>
	 * 
	 * @param file Object of class {@link AttachedFile} to be persisted
	 */
	void save (AttachedFile file);	
	
	/**
	 * Remove the file from the storage container. <p>
	 * 
	 * @param id unique identifier of the file
	 */
	void remove (String id);
	
	/**
	 * Removes files belonging to a library. <p>
	 * 
	 * @param libraryId Unique identifier of the files library
	 */
	void removeLibrary (String libraryId);
	
	/**
	 * Find AttachedFile by primary key. <p>
	 * 
	 * @param  id unique identifier of the file
	 * @return An instance of the file found or null if the file does not exist 
	 */
	AttachedFile find (String id);
	
	/**
	 * Find files belonging to a library. <p>
	 * 
	 * @param libraryId Unique identifier of the files library
	 * @return Collection of objects of the class {@link AttachedFile}
	 */
	Collection<AttachedFile> findLibrary (String libraryId);
}
