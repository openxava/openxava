# OpenXava

Low-Code Platform for Rapid Development of Enterprise Web Applications. Write just the domain classes in plain Java. Get a web application ready for production.

Oficial web : https://www.openxava.org/

# Features

- No code generation: touch your code try your application in a few seconds.
- Although the User Interface is automatically generated (on fly) a fine tuning front-end arrangement is allowed.
- Adapted to work with legacy database schemas.
- Easy integration of reports made with JasperReports.
- Exhaustive documentation in English, French, Russian, Chinese and Spanish.

# Developer shell mode

- clone this repository
- Create a new project
```
ant -f OpenXava/CreateNewProjectDockerTomcat.xml
```
- Open it on your eclipse
- configure the database connection: `../persistence/META-INF/persistence.xml`
  - by default hsqldb is used
- Create some pojo classes in /src
- Run `../src/_run/AppLauncher.java`


# Developer IDE mode

- clone this repository
- open the openxava IDE
  - linux: `./openxava`
- create new project using the IDE
- configure the database connection: `../persistence/META-INF/persistence.xml`
  - by default hsqldb is used
- Create some pojo classes in /src
- Run `../src/_run/AppLauncher.java`

# Production Mode

If everything worked in developer mode you will have a light repository (without jars) ready to push it to some git repository.

> Note: Change `<property name="hibernate.hbm2ddl.auto" value="create-drop"/>` to validate or just delete it before production deployment

## Docker

This repository can be built and deploy with docker. For more details click [here](OpenXava/project-templates/en-docker-tomcat/readme.md).

You can integrate this repository with any devops flow.

## Manual mode (without devops)

- Compile the app using ant
- Generate war using ant
- Download and configure java and tomcat server
- move the generated war to webapps folder in tomcat

# Roadmap

- Modify to [hibernate-core](https://github.com/jrichardsz/hibernate-orm/tree/5.3.9-Final-Feature_env) to be able to use environment variables (instead harcoded values in persistence.xml or hibernate.cfg.xml) which is the best option for devops and automation purposes (a human will not necessary to change these values in the sever). This is used by [spring](https://stackoverflow.com/a/35535138/3957754) and a lot of platforms support it like [heroku](https://devcenter.heroku.com/articles/config-vars) because you can configure your application from outside, just sending the right variables
- Split this repository into several maven projects with its own github repository and publish them in https://mvnrepository.com/
  - Openxava -> openxava-core
  - Addons -> openxava-addons
  - ox-jdbc-adapters
- Create a new template in Openxava/project-templates with maven structure which will use the previous public maven dependencies instead the local jars (openxava.jar, addons.jar, ox-jdbc-adapters.jar). This new template will be more light and modern
- Add the usage of `JAVA_` variables to configure the tomcat server without touch it
- Modify jpa annotations and hibernate engine to support database comments and PK custom name. Currently comments are not suported and pks has random names.
- Add docker-compose
- Add the openxava image log to this readme
