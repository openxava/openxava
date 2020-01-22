package org.openxava.web.editors;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.commons.io.*;
import org.apache.commons.logging.*;
import org.openxava.util.*;

import com.openxava.naviox.util.*;

public class AmazonS3Persistor {
	
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
  
}
