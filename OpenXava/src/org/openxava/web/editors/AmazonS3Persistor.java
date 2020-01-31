package org.openxava.web.editors;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.commons.io.*;
import org.apache.commons.io.filefilter.*;
import org.apache.commons.logging.*;
import org.openxava.calculators.*;
import org.openxava.util.*;

import com.openxava.naviox.util.*;

public class AmazonS3Persistor implements IFilePersistor {

   private static final String SEPARATOR     = "_OX_";
   private static final java.io.File PARENT  = new java.io.File(XavaPreferences.getInstance().getFilesPath());
   private static final String PrefixFolderName = NaviOXPreferences.getInstance().getAmazonBucketPrefixFolderName();
   private static Log log = LogFactory.getLog(AmazonS3Persistor.class); 
   
   public static Object initialize() {
	   
	   /** 
	    * Amazon s3 credentials 
	    */
	   String accessKey = NaviOXPreferences.getInstance().getAmazonAccessKey();
	   
	   String secretKey = NaviOXPreferences.getInstance().getAmazonSecretKey();	   
	   
	   String region = NaviOXPreferences.getInstance().getAmazonRegion();
	   try {
	       
	       Class<?> awsClientBuilder = Class.forName("com.amazonaws.client.builder.AwsClientBuilder");
	       Object result = Class.forName("com.amazonaws.services.s3.AmazonS3ClientBuilder")
	       						.getMethod("standard")
	       						.invoke(null);
	       
	       if (!secretKey.isEmpty() && !accessKey.isEmpty()) {
	    	   Object credentials = Class.forName("com.amazonaws.auth.BasicAWSCredentials")
		   				.getConstructor(String.class, String.class)
  						.newInstance(accessKey, secretKey);
		       
	    	   Object awsStaticCredentialsProvider = Class.forName("com.amazonaws.auth.AWSStaticCredentialsProvider")
						.getConstructor(Class.forName("com.amazonaws.auth.AWSCredentials"))
						.newInstance(credentials);
		       
	    	   result = awsClientBuilder.getMethod("withCredentials", Class.forName("com.amazonaws.auth.AWSCredentialsProvider"))
	    			   					   .invoke(result, awsStaticCredentialsProvider);
	    	   
	    	   result = awsClientBuilder.getMethod("withRegion", String.class).invoke(result, region);
	       }
	       
	       return awsClientBuilder.getMethod("build").invoke(result);
	        
	   } catch(Exception ex) {
		   
		 ex.printStackTrace();
		 return null;
	   
	   }
   }
	  
   public static String amazonBucketName() {
	   /** Amazon s3 Bucket Name */
	   return NaviOXPreferences.getInstance().getAmazonBucketName();
   
   }
	  
   public static void uploadToAmazonS3(String filename) {
	   try {
       	   
		   /** 
		    * Initializing the Amazon S3 Client 
		    * */
		   Object s3 = initialize();
		   
		   /** Amazon S3 Bucket name */
	       String bucketName = amazonBucketName();
           
	       /**
	        *  Uploading a new object to S3 from a file..
	        *  1) Creating a file object using the uploaded filename
	        *  2) Uploading the file to s3 with bucketname, filename as key and file object. 
	        */
	       
	       File file = new File( PARENT +"/"+ filename.toString());

	       log.debug("Uploading a new object to S3 from a file" + file.getName());         
	       
	       Class putObjectRequestCls = Class.forName("com.amazonaws.services.s3.model.PutObjectRequest");
	       Constructor putObjectRequestCons = putObjectRequestCls.getConstructor(String.class, String.class, File.class);
	       Object putObjectRequest = putObjectRequestCons.newInstance(bucketName, PrefixFolderName+file.getName(), file);
	       
	       Method putObject = s3.getClass().getMethod("putObject", putObjectRequestCls);
	       putObject.invoke(s3, putObjectRequest);
           
           /** Deleting the new file stored in local directory */
           FileUtils.deleteQuietly(file);
           
       } catch (Exception ex) {
		   
    	   ex.printStackTrace();
       
       } 
   }
   
   
   public static void downloadFromAmazonS3(String uuid) throws IOException {
	   try {
		  
			/** Initializing the Amazon S3 Client Connection with access key and secret key */
			Object s3 = initialize();
			
			/** Amazon S3 Bucket name */
			String bucketName = amazonBucketName();
			 
			/** 
			 * Fetching all to objects with prefix with unique uuid 
			 * Note: Always only one object will be present in  
			 */
			   
			Method listObjects = s3.getClass().getMethod("listObjects", String.class, String.class);
			Object s3Objects = listObjects.invoke(s3, bucketName, PrefixFolderName+uuid);
					
			/**
			 *  Downloading the file from s3..
			 *  1) Loop through the Objectlisting and get key to get the object.
			 *  2) Copy the content to local directory. 
			 */
			Method getObjectSummaries = s3Objects.getClass().getMethod("getObjectSummaries");
			List<Object> s3ObjectSummaries = (List<Object>) getObjectSummaries.invoke(s3Objects);
			
			
			for (Object objectSummary: s3ObjectSummaries) {
				
				String key = (String) objectSummary.getClass().getMethod("getKey").invoke(objectSummary);
				
				Class getObjectRequestCls = Class.forName("com.amazonaws.services.s3.model.GetObjectRequest");
				Constructor getObjectRequest = getObjectRequestCls.getConstructor(String.class, String.class);
				
				Object s3object = s3.getClass()
				                    .getMethod("getObject", getObjectRequestCls)
				                    .invoke(s3, getObjectRequest.newInstance(bucketName, key));
				
				Object inputStream = s3object.getClass().getMethod("getObjectContent").invoke(s3object);
				
				String keyWithoutPrefix = (PrefixFolderName.isEmpty()) ? key : key.split(PrefixFolderName)[1];		
				
				FileUtils.copyInputStreamToFile((InputStream) inputStream, new File(PARENT+"/"+keyWithoutPrefix));
				
			}
			
		} catch (Exception ex) {
			
			ex.printStackTrace();
		
		} 
   }
  
   
   public static void deleteFromAmazonS3(String uuid) {
	   
	   try {
		   
			/** Initializing the Amazon S3 Client Connection with access key and secret key */
			Object s3 = initialize();
			/** Amazon S3 Bucket name */
			String bucketName = amazonBucketName();
			
			Class deleteObjectRequestCls = Class.forName("com.amazonaws.services.s3.model.DeleteObjectRequest");
			
			Constructor deleteObjectRequestCons = deleteObjectRequestCls.getConstructor(String.class, String.class);
			
			Object deleteObjectRequest = deleteObjectRequestCons.newInstance(bucketName, PrefixFolderName + uuid);
			
			Method deleteObject = s3.getClass().getMethod("deleteObject", deleteObjectRequestCls);
			
			Object s3Objects = deleteObject.invoke(s3, deleteObjectRequest);
	
	   } catch (Exception e) {
		// TODO: handle exception
	}
   }


   @Override
   public void save(AttachedFile file) {
	// TODO Auto-generated method stub
		try {
			String uuid = (String) new UUIDCalculator().calculate();
			StringBuilder filename = new StringBuilder(); //filename = UUID_OX_FILENAME_OX_LIBRARYID			
			filename.append(uuid).append(SEPARATOR).append(file.getName()).append(SEPARATOR);
			filename.append(Is.emptyString(file.getLibraryId()) ? "NOLIBRARY" : file.getLibraryId());
			FileUtils.writeByteArrayToFile(new java.io.File(PARENT, filename.toString()), file.getData());
			file.setId(uuid);
			uploadToAmazonS3(filename.toString());          
		} catch(Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new RuntimeException("save_file_error");
		}
   }


   @Override
   public void remove(String id) {
	   java.io.File f;
		try {
			f = findIOFile(id);
			if(f == null) return;
			deleteFromAmazonS3(id);
			FileUtils.deleteQuietly(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
 
   @Override
   public void removeLibrary(String libraryId) {
	// TODO Auto-generated method stub
		Collection<java.io.File> ioFiles = FileUtils.listFiles(PARENT, 
					FileFilterUtils.suffixFileFilter(libraryId), null);
		for(Iterator<java.io.File> it = ioFiles.iterator(); it.hasNext(); ) {
			String[] id = it.next().getName().split("_");
			deleteFromAmazonS3(id[0]);
			FileUtils.deleteQuietly(it.next());
		}
   }

   @Override
   public AttachedFile find(String id) {
	// TODO Auto-generated method stub
		java.io.File f;
		try {
			f = findIOFile(id);
			if(f == null) return null;
			return convertIOFileToOXFile(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
   }

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

   private java.io.File findIOFile(String uuid) throws IOException{
		Collection<java.io.File> files = FileUtils.listFiles(PARENT, 
									FileFilterUtils.prefixFileFilter(uuid), null);
		if(files.size() == 1) return files.iterator().next();
		if(files.size() > 1) log.warn(XavaResources.getString("multiple_file_matches", uuid));
		if (XavaPreferences.getInstance().isSaveToS3Enabled() == true){
			if(files.size() == 0) {
				downloadFromAmazonS3(uuid); 
				Collection<java.io.File> files1 = FileUtils.listFiles(PARENT, 
						FileFilterUtils.prefixFileFilter(uuid), null);
				if(files1.size() == 1) return files1.iterator().next();
				if(files1.size() > 1) log.warn(XavaResources.getString("multiple_file_matches", uuid));
			}
		}
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
