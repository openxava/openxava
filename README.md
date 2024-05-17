# OpenXava

OpenXava generates a fully functional web application from JPA entities.

## Prerequisites
You need Java and Maven installed

## Create your first project
From command line prompt type:

	mvn archetype:generate -DarchetypeGroupId=org.openxava -DarchetypeArtifactId=openxava-archetype -DarchetypeVersion=RELEASE -DgroupId=com.yourcompany -DartifactId=yourapp -DinteractiveMode=false

 Then change to the folder where the project has been created:
 
 	cd yourapp

And build the project:
  
  	mvn package

## Run your application
In Windows:

	java -cp "target/yourapp/WEB-INF/classes;target/yourapp/WEB-INF/lib/*" com.yourcompany.yourapp.run.yourapp
	
In Linux/Mac:

	java -cp "target/yourapp/WEB-INF/classes:target/yourapp/WEB-INF/lib/*" com.yourcompany.yourapp.run.yourapp

Open your browser and go to http://localhost:8080/yourapp

## Modify the code
Stop your application with Ctrl-C in the prompt. Then use your favorite editor to edit YourFirstEntity.java in yourapp/src/main/java/com/yourcompany/yourapp/model.
Have a look at the code, and modify it, add a new property, for example. 

When code is ready compile:

	mvn compile
	
And start the application again.	

## Learn more
To learn more go to the official openxava documentation here: [openxava.org/doc](https://openxava.org/doc)
