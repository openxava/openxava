# OpenXava

OpenXava generates a fully functional web application from JPA entities.

## Prerequisites
You need Java and Maven installed

## Create your first project
From command line prompt type:

	mvn archetype:generate -DarchetypeGroupId=org.openxava -DarchetypeArtifactId=openxava-archetype -DarchetypeVersion=RELEASE -DgroupId=com.yourcompany -DartifactId=yourapp -DinteractiveMode=false
 	cd yourapp
  	mvn package
   	

## Run your application
In Windows:

	java -cp "target/yourapp/WEB-INF/classes;target/yourapp/WEB-INF/lib/*" com.yourcompany.yourapp.run.yourapp
	
In Linux/Mac:

	java -cp "target/yourapp/WEB-INF/classes:target/yourapp/WEB-INF/lib/*" com.yourcompany.yourapp.run.yourapp

Open your browser and go to http://localhost:8080/yourapp

## Modify the code
Using your favorite editor edit YourFirstEntity.java in yourapp/src/main/java/com/yourcompany/yourapp/model.
Have a look at the code, and modify it, add a new property, for example. Then compile the code:

	mvn compile
	
And stop and start the application again.	

## Learn more
To learn more go to official openxava documentation here: [openxava.org/doc](https://openxava.org/doc)
