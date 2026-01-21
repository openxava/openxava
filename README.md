# OpenXava

OpenXava generates a fully functional web application from JPA entities.

## Prerequisites
You need Java and Maven installed.

## Create your first project
From the command-line prompt, type:

	mvn archetype:generate -DarchetypeGroupId=org.openxava -DarchetypeArtifactId=openxava-archetype -DarchetypeVersion=RELEASE -DgroupId=com.yourcompany -DartifactId=yourapp -DinteractiveMode=false

Then change to the folder where the project was created:
 
	cd yourapp

And build the project:
  
  	mvn package

## Run your application

	mvn exec:java
	
Open your browser and go to http://localhost:8080/yourapp

## Modify the code
Stop your application with Ctrl+C in the prompt. Then use your favorite editor to edit YourFirstEntity.java in yourapp/src/main/java/com/yourcompany/yourapp/model.
Have a look at the code and modify it. Add a new property, for example. 

When the code is ready, compile it:

	mvn compile
	
Then start the application again.	

## New project from other archetypes
Apart from the basic archetype, there are other OpenXava archetypes, so you don't have to start your application from scratch.
For a project with an initial Master-Detail structure, use openxava-master-detail-archetype, in this way:

	mvn archetype:generate -DarchetypeGroupId=org.openxava -DarchetypeArtifactId=openxava-master-detail-archetype -DarchetypeVersion=RELEASE -DgroupId=com.yourcompany -DartifactId=yourmasterdetail -DinteractiveMode=false
	
For a basic CRM use openxava-crm-archetype, thus:

	mvn archetype:generate -DarchetypeGroupId=org.openxava -DarchetypeArtifactId=openxava-crm-archetype -DarchetypeVersion=RELEASE -DgroupId=com.yourcompany -DartifactId=yourcrm -DinteractiveMode=false
	
For a project management application use openxava-project-management-archetype:

	mvn archetype:generate -DarchetypeGroupId=org.openxava -DarchetypeArtifactId=openxava-project-management-archetype -DarchetypeVersion=RELEASE -DgroupId=com.yourcompany -DartifactId=yourtracker -DinteractiveMode=false
	
For an invoicing application use openxava-invoicing-archetype:

	mvn archetype:generate -DarchetypeGroupId=org.openxava -DarchetypeArtifactId=openxava-invoicing-archetype -DarchetypeVersion=RELEASE -DgroupId=com.yourcompany -DartifactId=yourinvoicing -DinteractiveMode=false	

## Java 25 support
If you want to use Java 25, you have to uncomment the `maven-compiler-plugin` section in the `pom.xml` of your project. This section is included by default but commented out. It ensures that annotation processing (Lombok) works correctly with Java 25.

## Older versions
To create a project from an older version of OpenXava, use the following command:

	mvn archetype:generate -DarchetypeGroupId=org.openxava -DarchetypeArtifactId=openxava-archetype -DarchetypeVersion=7.6.3 -DgroupId=com.yourcompany -DartifactId=yourapp -DinteractiveMode=false

Note that you can replace 7.6.3 with any other version you want to use.

To execute applications of version 7.6.3 or older, use Java directly. For Windows:

	java -cp "target/yourapp/WEB-INF/classes;target/yourapp/WEB-INF/lib/*" com.yourcompany.yourapp.run.yourapp

For Linux/Mac:

	java -cp "target/yourapp/WEB-INF/classes:target/yourapp/WEB-INF/lib/*" com.yourcompany.yourapp.run.yourapp

## Learn more
To learn more, go to the official OpenXava documentation here: [openxava.org/doc](https://openxava.org/doc)
