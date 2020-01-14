package org.openxava.web.editors;

import java.io.*;

import org.apache.commons.io.*;
import org.openxava.util.*;

import com.amazonaws.*;
import com.amazonaws.auth.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;
import com.openxava.naviox.util.*;

public class AmazonS3Persistor {
	
   private static final java.io.File PARENT  = new java.io.File(XavaPreferences.getInstance().getFilesPath());
   private static final String PrefixFolderName = NaviOXPreferences.getInstance().getAmazonBucketPrefixFolderName();
   private static Log log = LogFactory.getLog(AmazonS3Persistor.class);
   
   public static AmazonS3Client initialize() {
	   
	   /** 
	    * Amazon s3 credentials 
	    */
	   String accessKey = NaviOXPreferences.getInstance().getAmazonAccessKey();
	   
	   String secretKey = NaviOXPreferences.getInstance().getAmazonSecretKey();
	   
	   String region = NaviOXPreferences.getInstance().getAmazonRegion();
	   
	   /**
	    * Building a s3 client without credentials using IAM role based access
	    */
       	   if (secretKey.isEmpty() || accessKey.isEmpty()) {
		   AmazonS3Client s3 = (AmazonS3Client) AmazonS3ClientBuilder
         	      .standard()
         	      .build();
		   return s3;
	    }	   

           /**
	    * Building a s3 client using credentials and region
	    */
           BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
	   
	   AmazonS3Client s3 = (AmazonS3Client) AmazonS3ClientBuilder
		            	      .standard()
		            	      .withCredentials(new AWSStaticCredentialsProvider(credentials))
		            	      .withRegion(region)
		            	      .build();
	   
	   return s3;
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
		   AmazonS3Client s3 = initialize();
		   
		   /** Amazon S3 Bucket name */
	       String bucketName = amazonBucketName();
           
	       /**
	        *  Uploading a new object to S3 from a file..
	        *  1) Creating a file object using the uploaded filename
	        *  2) Uploading the file to s3 with bucketname, filename as key and file object. 
	        */
	       
	       File file = new File( PARENT +"/"+ filename.toString());

	       System.out.println("Uploading a new object to S3 from a file" + file.getName());         
	       
	       s3.putObject(new PutObjectRequest(bucketName, PrefixFolderName+file.getName(), file));
           
           /** Deleting the new file stored in local directory */
           FileUtils.deleteQuietly(file);
           
       } catch (AmazonServiceException ase) {
    	   
    	   amazonException(ase);
       
       } 
	   	catch (AmazonClientException ace) {
    	   
	   		amazonClientException(ace);
       
	   	} 
   }
   
   
   public static void downloadFromAmazonS3(String uuid) throws IOException {
	   try 
		{
		    /** Initializing the Amazon S3 Client Connection with access key and secret key */
			AmazonS3Client s3 = initialize();
			
			/** Amazon S3 Bucket name */
	        String bucketName = amazonBucketName();
	         
	        /** 
	         * Fetching all to objects with prefix with unique uuid 
	         * Note: Always only one object will be present in  
	         */
	        
			ObjectListing s3Objects = s3.listObjects(bucketName, PrefixFolderName+uuid);
			
			/**
		     *  Downloading the file from s3..
		     *  1) Loop through the Objectlisting and get key to get the object.
		     *  2) Copy the content to local directory. 
		     */
			
			for (S3ObjectSummary objectSummary: s3Objects.getObjectSummaries()) {
			
				String key = objectSummary.getKey();
		        
				S3Object s3object = s3.getObject(new GetObjectRequest(bucketName, key));
				
				S3ObjectInputStream inputStream = ((S3Object) s3object).getObjectContent();
			    
				String keyWithoutPrefix = (PrefixFolderName.isEmpty()) ? key : key.split(PrefixFolderName)[1];		
				
				FileUtils.copyInputStreamToFile(inputStream, new File(PARENT+"/"+keyWithoutPrefix));
    			
			}
		} catch (AmazonServiceException ase) {
			
			amazonException(ase);
        
		} catch (AmazonClientException ace) {
    	   
        	amazonClientException(ace);
        }  
   }
   
   public static void amazonException(AmazonServiceException ase) {
	log.error("Caught an AmazonServiceException, which means your request made it "
	               + "to Amazon S3, but was rejected with an error response for some reason.");
	log.debug("Error Message:    " + ase.getMessage());
	log.debug("HTTP Status Code: " + ase.getStatusCode());
	log.debug("AWS Error Code:   " + ase.getErrorCode());
	log.debug("Error Type:       " + ase.getErrorType());
	log.debug("Request ID:       " + ase.getRequestId());
   }
   
   public static void amazonClientException(AmazonClientException ace) {
	log.error("Caught an AmazonClientException, which means the client encountered "
               + "a serious internal problem while trying to communicate with S3, "
               + "such as not being able to access the network.");
	log.debug("Error Message: " + ace.getMessage());
   }

}
