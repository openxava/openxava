package org.openxava.web.editors;

import java.io.*;
import java.util.*;

import org.apache.commons.io.*;
import org.apache.commons.io.filefilter.*;
import org.apache.commons.logging.*;
import org.openxava.calculators.*;
import org.openxava.util.*;

/**
 * A implementation of {@link IFilePersistor} <p>
 * 
 * @author Jeromy Altuna
 */
public class FileSystemPersistor implements IFilePersistor {
	
	private static final String SEPARATOR     = "_OX_";
	private static final java.io.File PARENT  = new java.io.File(XavaPreferences.getInstance().getFilesPath());
	
	private static Log log = LogFactory.getLog(FileSystemPersistor.class);
	
	static {
		 try {
			 if(!PARENT.mkdir() && !PARENT.exists())
				 throw new RuntimeException(
					String.format("java.io.File(%1$s).mkdir()==false && java.io.File(%1$s).exists()==false", 
							      PARENT.getAbsolutePath()));
		 }catch(Exception ex) {
			 log.error(ex.getMessage(), ex);
			 throw new RuntimeException("file_system_persistor_error");
		 }		 
	}	
	
	/**
	 * @see org.openxava.web.editors.IFilePersistor#save(AttachedFile)
	 */
	@Override
	public void save(AttachedFile file) {
		try {
			String uuid = (String) new UUIDCalculator().calculate();
			StringBuilder filename = new StringBuilder(); //filename = UUID_OX_FILENAME_OX_LIBRARYID			
			filename.append(uuid).append(SEPARATOR).append(file.getName()).append(SEPARATOR);
			filename.append(Is.emptyString(file.getLibraryId()) ? "NOLIBRARY" : file.getLibraryId());
			FileUtils.writeByteArrayToFile(new java.io.File(PARENT, filename.toString()), file.getData());
			file.setId(uuid);
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RuntimeException("save_file_error");
		}
	}
	
	/**
	 * @see org.openxava.web.editors.IFilePersistor#remove(String)
	 */
	@Override
	public void remove(String id) {
		java.io.File f = findIOFile(id);
		if(f == null) return;
		FileUtils.deleteQuietly(f);
	}
	
	/**
	 * @see org.openxava.web.editors.IFilePersistor#removeLibrary(String)
	 */
	@Override
	public void removeLibrary(String libraryId) {
		Collection<java.io.File> ioFiles = FileUtils.listFiles(PARENT, 
							FileFilterUtils.suffixFileFilter(libraryId), null);
		for(Iterator<java.io.File> it = ioFiles.iterator(); it.hasNext(); ) {
			FileUtils.deleteQuietly(it.next());
		}
	}
	
	/**
	 * @see org.openxava.web.editors.IFilePersistor#find(String)
	 */
	@Override
	public AttachedFile find(String id) {
		java.io.File f = findIOFile(id);
		if(f == null) return null;
		return convertIOFileToOXFile(f);
	}
	
	/**
	 * @see org.openxava.web.editors.IFilePersistor#findLibrary(String)
	 */
	@Override
	public Collection<AttachedFile> findLibrary(String libraryId) {
		Collection<java.io.File> ioFiles = FileUtils.listFiles(PARENT, 
							FileFilterUtils.suffixFileFilter(libraryId), null);
		Collection<AttachedFile> oxFiles = new ArrayList<AttachedFile>();
		for(Iterator<java.io.File> it = ioFiles.iterator(); it.hasNext(); ) {
			oxFiles.add(convertIOFileToOXFile(it.next()));
		}
		return oxFiles;
	}
	
	private java.io.File findIOFile(String uuid) {
		Collection<java.io.File> files = FileUtils.listFiles(PARENT, 
									FileFilterUtils.prefixFileFilter(uuid), null);
		if(files.size() == 1) return files.iterator().next();
		if(files.size() > 1) log.warn(XavaResources.getString("multiple_file_matches", uuid));
		return null;
	}
	
	private AttachedFile convertIOFileToOXFile(java.io.File f) {
		String[] parts = f.getName().split(SEPARATOR);
		AttachedFile file = new AttachedFile();
		file.setId(parts[0]);
		file.setName(parts[1]);
		file.setLibraryId(parts[2]);
		try {
			file.setData(FileUtils.readFileToByteArray(f));
		} catch (IOException ex) {
			log.error(ex.getMessage(), ex);
			throw new RuntimeException(XavaResources.getString("convert_iofile_to_oxfile_error"));
		}
		return file;
	}
}
