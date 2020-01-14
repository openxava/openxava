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
```
- Extra property: To prefix with new folder you can add folder name with this property.
```
bucketPrefixFolderName=bucketPrefixFolderName
```

## If IAM role based access is used then accessKey and secretkey are not required as mandatory


## Need any help for more advanced use cases / support?

- Contact us via sales@mahaswami.com
