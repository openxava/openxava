# Amazon S3 Persistance for OpenXava using Java Reflection

## Introduction:
  - Openxava has two ways of storing a file. 
    - File system persistor which stores the file in the server/local. 
    - JPAFilePersistor which stores file in the database. 

  - The most common practice used these days to persist document/files is to store it in amazon S3 storage which provides very high reliability, durability, and security. 

  - In addition to the current two methods this patch introduces ```Amazon S3 persistence```
  
## Steps
  
   ## Step 1
   - Enable ```amazonS3Persistor``` in xava.properties
   ```
   amazonS3Persistor=true
   ```
   ## Step 2
   - Download the below jars and place it in web/WEB-INF/lib/
   
	   - Aws sdk jars

	   ```

	   - aws-java-sdk-core.jar (https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk-core)
	   - aws-java-sdk-s3.jar (https://mvnrepository.com/artifact/com.amazonaws/aws-java-sdk-s3)  

	   ```

	   - Aws sdk dependency jars

	   ```
	   - jackson-annotations.jar (https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations) 
	   - jackson-core.jar (https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core)
	   - jackson-databind.jar (https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind)
	   - jackson-dataformat.jar (https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-xml)
	   - joda-time.jar (https://mvnrepository.com/artifact/joda-time/joda-time)
	   ```
   
 
  ## Step 3
  - Configure S3 credential

	- Amazon S3 requires few properties for making request using AWS SDK
	```
	  Region
	  Bucket Name
	  Access key ID
	  Secret access key
	```
	
   	- The following properties can be configured in naviox.properties
	```
	bucketName=bucketName
	accessKey=accessKey
	secretKey=secretKey
	region=region
	```

   	- Extra property: To prefix with new folder you can add folder name with this property.
	```
	bucketPrefixFolderName=bucketPrefixFolderName
	```

## Note
	If IAM role based access is used then region, accessKey and secretkey are not required
