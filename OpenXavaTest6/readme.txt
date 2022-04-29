To properly run these tests you must perform several configurations.

==============================
Configure Database server
==============================
You must have a HSQLDB server enabled prior to start the application.
Create a new run configuration with the following parameters:
Project: OpenXavaTest
Main Class: org.hsqldb.Server
Program Arguments: -database.0 data/openxava-test-db -port 1555

==============================
Configure the Server context
==============================
In the context configuration, located in ${tomcat}/conf/context.xml
  or under Servers project in Eclipse; 
  you should have in context.xml with the following resource definition:

    <Resource auth="Container" driverClassName="org.hsqldb.jdbcDriver" 
    	maxActive="20" maxIdle="5" maxWait="10000" 
    	name="jdbc/OpenXavaTestDS" password="" 
    	type="javax.sql.DataSource" 
    	url="jdbc:hsqldb:hsql://localhost:1555" 
    	username="sa"/>
==============================
Configure the Tomcat server
==============================
You must add the default language to the Tomcat Server.
Edit the server configuration and open its launch parameters.
Add the following jvm startup properties:
-Duser.country=es -Duser.language=ES

==============================
Running the tests
==============================
You can run the tests individually using Eclipse IDE.
or the full test suite with the task 'runTests' provided
in build.xml located in the root folder of the project.
    	
