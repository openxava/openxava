# Amazon S3 Persistance for OpenXava

## Introduction:
  - Openxava has two ways of storing a file. 
    - File system persistor which stores the file in the server/local. 
    - JPAFilePersistor which stores file in the database. 
           
  - The most common practice used these days to persist document/files is to store it in amazon S3 storage which provides very high reliability, durability, and security. 
  
  - In addition to the current two methods this patch introduces ```Amazon S3 persistence```
    
## Steps to Run
- The following jars required to integrate amazon S3

```
   aws-java-sdk-2.10.47-javadoc.jar
   aws-java-sdk-2.10.47-sources.jar
   aws-java-sdk-2.10.47.jar
   jackson-annotations-2.10.0.jar
   jackson-core-2.10.0.jar
   jackson-databind-2.10.0.jar
   jackson-dataformat-xml-2.10.0.jar
   joda-time-2.10.5.jar
```
- I Suggest to use latest aws sdk jar version.

## Configure S3 credential

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

  - **Example: Using above credentials the Client is build**

```
	    BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		
   	    AmazonS3Client s3 = (AmazonS3Client) AmazonS3ClientBuilder
		            	      .standard()
		            	      .withCredentials(new AWSStaticCredentialsProvider(credentials))
		            	      .withRegion(region)
		            	      .build();
```

## If IAM role based access is used then region, accessKey and secretkey are not required

 - **Example: Building client without region, accessKey and secretkey**

```
   	    AmazonS3Client s3 = (AmazonS3Client) AmazonS3ClientBuilder
		            	      .standard()
		            	      .build();
```

## Need any help for more advanced use cases / support?

- Contact us via sales@mahaswami.com
