package org.openxava.web.editors;

import static org.openxava.jpa.XPersistence.*;

import java.util.*;

import javax.persistence.*;

/**
 * An implementation of {@link IFilePersistor} <p>
 * 
 * @author Jeromy Altuna
 */
public class JPAFilePersistor implements IFilePersistor {
	
	/**
	 * @see org.openxava.web.editors.IFilePersistor#save(AttachedFile)
	 */
	@Override
	public void save(AttachedFile file) {
		getManager().persist(file);
		commit();
	}
	
	/**
	 * @see org.openxava.web.editors.IFilePersistor#remove(String)
	 */
	@Override
	public void remove(String id) {
		AttachedFile file = getManager().find(AttachedFile.class, id);
		if (file == null) return; 
		getManager().remove(file);
		commit();
	}
	
	/**
	 * @see org.openxava.web.editors.IFilePersistor#removeLibrary(String)
	 */
	@Override
	public void removeLibrary(String libraryId) {
		Query query = getManager().createQuery("delete from AttachedFile f where "
											 + "f.libraryId = :libraryId");
		query.setParameter("libraryId", libraryId);
		query.executeUpdate();
		commit();
	}
	
	/**
	 * @see org.openxava.web.editors.IFilePersistor#findLibrary(String)
	 */
	@Override
	public Collection<AttachedFile> findLibrary(String libraryId) {
		TypedQuery<AttachedFile> query = getManager().createQuery(
											   "from AttachedFile f " +
											   "where f.libraryId = :libraryId", 
											 	AttachedFile.class);
		query.setParameter("libraryId", libraryId);
		return query.getResultList();		
	}
	
	/**
	 * @see org.openxava.web.editors.IFilePersistor#find(String)
	 */
	@Override
	public AttachedFile find(String id) {
		AttachedFile file = getManager().find(AttachedFile.class, id);
		commit();		
		return file;
	}
}
